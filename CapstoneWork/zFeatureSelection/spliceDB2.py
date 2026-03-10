import pandas as pd

INPUT_FILE = "FeaturedSet_NegAndPosGoodRatio.csv"
OUTPUT_FILE = "160kPosAndNegNoFeatures.csv"

df = pd.read_csv(INPUT_FILE)


df = df.drop(columns=["Len1","Len2","LenDiff","LCS_Length","LCS_Ratio","Levenshtein","Shared_Chars","Jaccard_Sim"])


df.to_csv(OUTPUT_FILE, index=False)

print(f"Saved cleaned file as {OUTPUT_FILE}")
print(df.head())