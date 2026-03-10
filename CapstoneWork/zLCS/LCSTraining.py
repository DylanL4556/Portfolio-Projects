import pandas as pd
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier, GradientBoostingClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, accuracy_score


df = pd.read_csv("FeaturedSet_NegAndPosGoodRatio.csv", header=0, low_memory=False)


numeric_cols = [
    "LCS_Length","LCS_Ratio"
]

df[numeric_cols] = df[numeric_cols].apply(pd.to_numeric, errors="coerce")


df = df.dropna(subset=numeric_cols + ["Label"])


df["Label"] = df["Label"].astype(int)


X = df[numeric_cols]
y = df["Label"]


scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)


X_train, X_test, y_train, y_test = train_test_split(
    X_scaled, y, test_size=0.25, random_state=42, stratify=y
)


models = {
    "Logistic Regression": LogisticRegression(max_iter=1000),
    "Random Forest": RandomForestClassifier(n_estimators=300, random_state=42),
    "Gradient Boosting": GradientBoostingClassifier(random_state=42)
}


for name, model in models.items():
    model.fit(X_train, y_train)
    preds = model.predict(X_test)
    acc = accuracy_score(y_test, preds)
    print(f"\n{name} Accuracy: {acc:.3f}")
    print(classification_report(y_test, preds))

