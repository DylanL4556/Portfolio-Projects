import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
import pandas as pd



def load_lcs_ratios(filename):
    df = pd.read_csv(filename)


    df = df[df["Label"] == 1]


    ratios = pd.to_numeric(df["LCS_Ratio"], errors="coerce").dropna()
    return ratios.values



def visualize_ratios(ratios, save_path="LCSRatioDistribution_Label.jpg"):
    plt.figure(figsize=(10, 6))
    sns.histplot(ratios, bins=20, kde=True, edgecolor="black")
    plt.title("Distribution of LCS Ratio (Label 1 Only)", fontsize=16)
    plt.xlabel("LCS Ratio")
    plt.ylabel("Frequency")
    plt.grid(alpha=0.3)
    plt.tight_layout()
    plt.savefig(save_path, dpi=300)
    plt.show()


if __name__ == "__main__":
    ratios = load_lcs_ratios("FeaturedSet_NegAndPosGoodRatio.csv")
    print(f"Parsed {len(ratios)} LCS ratios with Label = 1.")
    print(f"Mean: {ratios.mean():.4f}")
    print(f"Median: {np.median(ratios):.4f}")
    print(f"Std Dev: {ratios.std():.4f}")

    visualize_ratios(ratios)
