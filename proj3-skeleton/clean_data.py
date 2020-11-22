import pickle


print("opening data files")

with open('data/y_train.pickle', 'rb') as handle:
    Y_train = pickle.load(handle)

with open('data/x_train.pickle', 'rb') as handle:
    X_train = pickle.load(handle)

X_train = X_train[:10]
for x in X_train:
    print(x)
