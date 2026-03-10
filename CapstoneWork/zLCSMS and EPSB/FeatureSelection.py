import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

from sklearn.feature_selection import (
    SelectKBest, chi2, f_classif, RFE
)
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split


df = pd.read_csv("160KLCSMS&EPSB_FeaturedSet.csv")


target = "Label"
X = df.drop(columns=[target, "Password1", "Password2"])
y = df[target]

scaler = MinMaxScaler()
X_scaled = scaler.fit_transform(X)


plt.figure(figsize=(14,10))
sns.heatmap(pd.DataFrame(X).corr(), cmap="coolwarm", annot=False)
plt.title(" Feature Correlation Heatmap")
plt.tight_layout()
plt.savefig("correlation_heatmap.png", dpi=300)
#plt.show()

chi_selector = SelectKBest(chi2, k="all")
chi_selector.fit(X_scaled, y)
chi_scores = pd.Series(chi_selector.scores_, index=X.columns).sort_values(ascending=False)

anova_selector = SelectKBest(f_classif, k="all")
anova_selector.fit(X_scaled, y)
anova_scores = pd.Series(anova_selector.scores_, index=X.columns).sort_values(ascending=False)

print("\n Chi^2 Feature Importance (Higher = Better) ")
print(chi_scores, "\n")

print("\n  ANOVA F-Score Importance ")
print(anova_scores, "\n")


rf = RandomForestClassifier(n_estimators=300, random_state=42)
rf.fit(X, y)

rf_importance = pd.Series(rf.feature_importances_, index=X.columns).sort_values(ascending=False)
print("\n  Random Forest Feature Importance ")
print(rf_importance, "\n")


rfe = RFE(RandomForestClassifier(n_estimators=200, random_state=42), n_features_to_select=10)
rfe.fit(X, y)

selected_features = X.columns[rfe.support_]
print("\n  RFE Selected Top 10 Features ")
print(list(selected_features), "\n")


