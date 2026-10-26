import sys, os, unittest
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "..", "src"))

from rescue_scoring import (
    RescueProfile, score_candidate, get_top_candidates,
    WATER_RESCUE_PROFILE,
)


class TestScoreCandidate(unittest.TestCase):

    def test_perfect_match_scores_100(self):
        animal = {"breed": "Newfoundland",
                   "age_upon_outcome_in_weeks": 80,
                   "sex_upon_outcome": "Intact Female"}
        self.assertEqual(score_candidate(animal, WATER_RESCUE_PROFILE), 100)

    def test_no_match_scores_zero(self):
        animal = {"breed": "Chihuahua",
                   "age_upon_outcome_in_weeks": 400,
                   "sex_upon_outcome": "Neutered Male"}
        self.assertEqual(score_candidate(animal, WATER_RESCUE_PROFILE), 0)

    def test_exact_breed_outscores_alias_breed(self):
        exact = {"breed": "Newfoundland",
                  "age_upon_outcome_in_weeks": 80,
                  "sex_upon_outcome": "Intact Female"}
        alias = {"breed": "Labrador Retriever",  # maps to an alias, not exact
                  "age_upon_outcome_in_weeks": 80,
                  "sex_upon_outcome": "Intact Female"}
        self.assertGreater(score_candidate(exact, WATER_RESCUE_PROFILE),
                            score_candidate(alias, WATER_RESCUE_PROFILE))

    def test_alias_breed_outscores_unrelated_breed(self):
        alias = {"breed": "Labrador Retriever",
                  "age_upon_outcome_in_weeks": 80,
                  "sex_upon_outcome": "Intact Female"}
        unrelated = {"breed": "Chihuahua",
                     "age_upon_outcome_in_weeks": 80,
                     "sex_upon_outcome": "Intact Female"}
        self.assertGreater(score_candidate(alias, WATER_RESCUE_PROFILE),
                            score_candidate(unrelated, WATER_RESCUE_PROFILE))

    def test_one_week_outside_range_still_scores_near_full_age_points(self):
        # Original hard filter would reject this animal outright (age 25,
        # one week under the 26-week cutoff). The scoring approach should
        # still rank it highly rather than excluding it.
        just_under = {"breed": "Newfoundland",
                      "age_upon_outcome_in_weeks": 25,
                      "sex_upon_outcome": "Intact Female"}
        in_range = {"breed": "Newfoundland",
                    "age_upon_outcome_in_weeks": 80,
                    "sex_upon_outcome": "Intact Female"}
        just_under_score = score_candidate(just_under, WATER_RESCUE_PROFILE)
        in_range_score = score_candidate(in_range, WATER_RESCUE_PROFILE)
        self.assertLess(just_under_score, in_range_score)   # slightly lower
        self.assertGreater(just_under_score, in_range_score - 5)  # but close
        self.assertGreater(just_under_score, 0)  # and NOT excluded entirely

    def test_age_score_floors_at_zero_far_outside_range(self):
        animal = {"breed": "Newfoundland",
                  "age_upon_outcome_in_weeks": 1,  # far below min_age (26)
                  "sex_upon_outcome": "Intact Female"}
        score = score_candidate(animal, WATER_RESCUE_PROFILE)
        # 40 (breed) + 0 (age, floored) + 30 (sex) = 70
        self.assertEqual(score, 70)

    def test_missing_fields_default_safely(self):
        self.assertEqual(score_candidate({}, WATER_RESCUE_PROFILE), 0)


class TestGetTopCandidates(unittest.TestCase):

    def _animal(self, name, breed, age, sex):
        return {"name": name, "breed": breed,
                "age_upon_outcome_in_weeks": age, "sex_upon_outcome": sex}

    def test_returns_k_results_in_descending_score_order(self):
        animals = [
            self._animal("A", "Newfoundland", 80, "Intact Female"),   # 100
            self._animal("B", "Chihuahua", 400, "Neutered Male"),      # 0
            self._animal("C", "Labrador Retriever", 80, "Intact Female"),  # 80
        ]
        top = get_top_candidates(animals, WATER_RESCUE_PROFILE, k=2)
        self.assertEqual([a["name"] for a in top], ["A", "C"])

    def test_k_larger_than_population_returns_all(self):
        animals = [self._animal("A", "Newfoundland", 80, "Intact Female")]
        top = get_top_candidates(animals, WATER_RESCUE_PROFILE, k=5)
        self.assertEqual(len(top), 1)

    def test_empty_population_returns_empty(self):
        self.assertEqual(get_top_candidates([], WATER_RESCUE_PROFILE, k=5), [])

    def test_k_zero_returns_empty(self):
        animals = [self._animal("A", "Newfoundland", 80, "Intact Female")]
        self.assertEqual(get_top_candidates(animals, WATER_RESCUE_PROFILE, k=0), [])

    def test_heap_result_matches_full_sort_result(self):
        # Correctness check: heap-based top-k must match a brute-force
        # full sort, not just "look reasonable"
        import random
        random.seed(42)
        breeds = ["Newfoundland", "Labrador Retriever Mix", "Chihuahua",
                  "Chesapeake Bay Retriever", "Poodle"]
        sexes = ["Intact Female", "Intact Male", "Neutered Male"]
        animals = [
            self._animal(f"animal{i}", random.choice(breeds),
                         random.randint(1, 400), random.choice(sexes))
            for i in range(500)
        ]
        k = 10
        heap_result = get_top_candidates(animals, WATER_RESCUE_PROFILE, k)
        full_sort_result = sorted(
            animals,
            key=lambda a: score_candidate(a, WATER_RESCUE_PROFILE),
            reverse=True,
        )[:k]
        heap_scores = [score_candidate(a, WATER_RESCUE_PROFILE) for a in heap_result]
        sort_scores = [score_candidate(a, WATER_RESCUE_PROFILE) for a in full_sort_result]
        self.assertEqual(heap_scores, sort_scores)


if __name__ == "__main__":
    unittest.main()
