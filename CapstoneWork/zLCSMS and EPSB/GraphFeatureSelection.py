import pandas as pd
import matplotlib.pyplot as plt
import numpy as np


chi2_scores = {
    "LCS_Ratio_1": 22287.875651,
    "Jaccard_Sim": 10220.007405,
    "LCS_1": 4726.217643,
    "Levenshtein": 1813.920770,
    "numbers1": 1435.925975,
    "caps1": 1268.395652,
    "numbers2": 1043.591225,
    "Shared_Chars": 967.961246,
    "LCS_Ratio_2": 957.682549,
    "caps2": 762.377583,
    "Len1": 537.498874,
    "LCS_2": 449.323340,
    "Len2": 409.349790,
    "LenDiff": 324.582148,
    "symbols2": 195.163475,
    "symbols1": 127.173970,
    "letters1": 124.825434,
    "letters2": 116.562366,
    "LCS_Ratio_3": 59.316888,
    "LCS_3": 45.725445,
    "low1": 37.091060,
    "low2": 18.888579
}

anova_scores = {
    "Levenshtein": 133587.467070,
    "LCS_Ratio_1": 53526.927980,
    "Len1": 51121.138519,
    "Jaccard_Sim": 45782.569730,
    "LCS_1": 41931.416668,
    "Len2": 39857.801829,
    "numbers1": 17096.260161,
    "numbers2": 13559.218086,
    "Shared_Chars": 11479.513130,
    "LenDiff": 10346.094471,
    "caps2": 9390.974775,
    "caps1": 8197.728297,
    "letters1": 5129.775037,
    "letters2": 3237.975739,
    "symbols2": 2996.560680,
    "LCS_Ratio_2": 2393.817073,
    "LCS_2": 1807.731198,
    "symbols1": 1686.419416,
    "low1": 1428.221616,
    "low2": 450.159129,
    "LCS_Ratio_3": 139.462241,
    "LCS_3": 101.547370
}

rf_scores = {
    "Levenshtein": 0.191954,
    "Jaccard_Sim": 0.118722,
    "Len1": 0.082806,
    "numbers1": 0.066026,
    "Len2": 0.064513,
    "numbers2": 0.064437,
    "LCS_Ratio_1": 0.060802,
    "caps2": 0.055270,
    "caps1": 0.052329,
    "LCS_1": 0.037733,
    "low1": 0.036049,
    "low2": 0.035569,
    "Shared_Chars": 0.031430,
    "letters2": 0.029955,
    "letters1": 0.029948,
    "LenDiff": 0.026153,
    "symbols2": 0.006516,
    "symbols1": 0.006506,
    "LCS_Ratio_2": 0.001925,
    "LCS_2": 0.001133,
    "LCS_Ratio_3": 0.000132,
    "LCS_3": 0.000092
}


def plot_feature_importance(scores_dict, title, filename, log_scale=False):
    features = list(scores_dict.keys())
    values = list(scores_dict.values())

    plt.figure(figsize=(16,6))
    plt.bar(features, values, color='skyblue', edgecolor='black')
    plt.title(title, fontsize=16)
    plt.xticks(rotation=45, ha='right')
    plt.ylabel("Importance Score")
    if log_scale:
        plt.yscale('log')
    plt.tight_layout()
    plt.savefig(filename, dpi=300)
    plt.close()


plot_feature_importance(chi2_scores, "Chi^2 Feature Importance", "chi2scoregraph.jpg", log_scale=True)
plot_feature_importance(anova_scores, "ANOVA F-Score Feature Importance", "anovascoregraph.jpg", log_scale=True)
plot_feature_importance(rf_scores, "Random Forest Feature Importance", "randomforestscoregraph.jpg", log_scale=False)
