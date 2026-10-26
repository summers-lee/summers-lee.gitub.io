"""
Weighted multi-criteria scoring and top-K selection for rescue-candidate
animals, replacing the original hard-cutoff MongoDB filters
(water_query, mountain_query, disaster_query) in ProjectTwoDashboard.

The original filters excluded an animal entirely if it missed any single
cutoff -- for example a dog one week outside the age range, or a breed
string that didn't exactly match the list. This module scores every
animal on a 0-100 scale across breed, age, and sex, so a near-miss still
ranks below a strong match instead of being thrown out.
"""

import heapq


class RescueProfile:
    """Defines what a rescue-training candidate should look like."""

    def __init__(self, preferred_breeds, min_age_weeks, max_age_weeks,
                 preferred_sex, breed_aliases=None):
        self.preferred_breeds = set(preferred_breeds)
        self.min_age_weeks = min_age_weeks
        self.max_age_weeks = max_age_weeks
        self.preferred_sex = preferred_sex
        # breed_aliases maps a breed string to the preferred breed it is
        # considered a near-match for (e.g. mixes, alternate spellings)
        self.breed_aliases = breed_aliases or {}


# Rescue profiles rebuilt from the dashboard's original hard-cutoff queries
WATER_RESCUE_PROFILE = RescueProfile(
    preferred_breeds=["Labrador Retriever Mix", "Chesapeake Bay Retriever",
                       "Newfoundland"],
    min_age_weeks=26,
    max_age_weeks=156,
    preferred_sex="Intact Female",
    breed_aliases={"Labrador Retriever": "Labrador Retriever Mix",
                    "Lab Mix": "Labrador Retriever Mix"},
)

MOUNTAIN_RESCUE_PROFILE = RescueProfile(
    preferred_breeds=["German Shepherd", "Alaskan Malamute",
                       "Old English Sheepdog", "Siberian Husky",
                       "Rottweiler"],
    min_age_weeks=26,
    max_age_weeks=156,
    preferred_sex="Intact Male",
)

DISASTER_RESCUE_PROFILE = RescueProfile(
    preferred_breeds=["Doberman Pinscher", "German Shepherd",
                       "Golden Retriever", "Bloodhound", "Rottweiler"],
    min_age_weeks=20,
    max_age_weeks=300,
    preferred_sex="Intact Male",
)


def _age_score(age_weeks, profile):
    """30 points inside the ideal range; a graceful falloff outside it
    instead of the original hard exclusion."""
    if profile.min_age_weeks <= age_weeks <= profile.max_age_weeks:
        return 30
    if age_weeks < profile.min_age_weeks:
        distance = profile.min_age_weeks - age_weeks
    else:
        distance = age_weeks - profile.max_age_weeks
    # Lose 2 points per week outside the range, floor at 0
    return max(0, 30 - 2 * distance)


def _breed_score(breed, profile):
    if breed in profile.preferred_breeds:
        return 40
    if profile.breed_aliases.get(breed) in profile.preferred_breeds:
        return 20  # partial credit for a known near-match
    return 0


def _sex_score(sex, profile):
    return 30 if sex == profile.preferred_sex else 0


def score_candidate(animal, profile):
    """Scores one animal dict against a RescueProfile. Returns 0-100."""
    return (
        _breed_score(animal.get("breed", ""), profile)
        + _age_score(animal.get("age_upon_outcome_in_weeks", 0), profile)
        + _sex_score(animal.get("sex_upon_outcome", ""), profile)
    )


def get_top_candidates(all_animals, profile, k):
    """
    Returns the top k highest-scoring animals for a rescue profile.

    Uses a size-k min-heap: O(n log k) for n animals, versus O(n log n)
    to fully sort the whole population first. Ties are broken by animal
    id (insertion order) so results are stable and comparable in tests.
    """
    if k <= 0:
        return []

    heap = []  # each entry: (score, tie_breaker, animal)
    for i, animal in enumerate(all_animals):
        s = score_candidate(animal, profile)
        entry = (s, i, animal)
        if len(heap) < k:
            heapq.heappush(heap, entry)
        elif s > heap[0][0]:
            heapq.heapreplace(heap, entry)
    # heap is a min-heap; return highest score first
    return [animal for (s, i, animal) in sorted(heap, reverse=True)]
