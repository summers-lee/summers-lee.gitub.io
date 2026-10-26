import sys, time, random
sys.path.insert(0, "src")
from rescue_scoring import get_top_candidates, score_candidate, WATER_RESCUE_PROFILE

def make_animals(n, seed=1):
    random.seed(seed)
    breeds = ["Newfoundland", "Labrador Retriever Mix", "Chihuahua",
              "Chesapeake Bay Retriever", "Poodle", "Labrador Retriever"]
    sexes = ["Intact Female", "Intact Male", "Neutered Male"]
    return [{"name": f"a{i}", "breed": random.choice(breeds),
             "age_upon_outcome_in_weeks": random.randint(1, 400),
             "sex_upon_outcome": random.choice(sexes)} for i in range(n)]

def full_sort_top_k(animals, profile, k):
    return sorted(animals, key=lambda a: score_candidate(a, profile), reverse=True)[:k]

k = 10
print(f"{'n':>8} | {'heap (s)':>10} | {'full sort (s)':>14} | heap result == sort result")
for n in [1_000, 10_000, 100_000, 500_000]:
    animals = make_animals(n)

    t0 = time.perf_counter()
    heap_result = get_top_candidates(animals, WATER_RESCUE_PROFILE, k)
    t1 = time.perf_counter()

    t2 = time.perf_counter()
    sort_result = full_sort_top_k(animals, WATER_RESCUE_PROFILE, k)
    t3 = time.perf_counter()

    heap_scores = [score_candidate(a, WATER_RESCUE_PROFILE) for a in heap_result]
    sort_scores = [score_candidate(a, WATER_RESCUE_PROFILE) for a in sort_result]
    match = heap_scores == sort_scores

    print(f"{n:>8} | {t1-t0:>10.5f} | {t3-t2:>14.5f} | {match}")
