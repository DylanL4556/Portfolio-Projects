import pandas as pd

CSV_FILE = "FeaturedSet_NegAndPosGoodRatio.csv"

counts = pd.read_csv(
    CSV_FILE,
    usecols=["Label"],
    dtype={"Label": "int8"}
)["Label"].value_counts()

print(counts)
