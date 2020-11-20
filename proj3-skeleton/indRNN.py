import pickle
import pandas as pd
import numpy as np
import tensorflow_datasets as tfds
import tensorflow as tf
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.callbacks import ModelCheckpoint
from tensorflow.keras.utils import Sequence
from sklearn.metrics import roc_curve, auc
import matplotlib.pyplot as plt

from ind_rnn import *


print("opening data files")
with open('data/vocab_set.pickle', 'rb') as handle:
    vocabulary_set = pickle.load(handle)


# Encode training, valid and test instances
encoder = tfds.features.text.TokenTextEncoder(vocabulary_set)

# Model Definition
model = tf.keras.Sequential([
    tf.keras.layers.Embedding(encoder.vocab_size, 64),
    tf.keras.layers.Bidirectional(IndRNN(64,  return_sequences=True)),
    tf.keras.layers.Bidirectional(IndRNN(32)),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dropout(0.5),
    tf.keras.layers.Dense(1, activation='sigmoid')
])

print("compiling models")
model.compile(loss='binary_crossentropy',
              optimizer=tf.keras.optimizers.Adam(1e-4),
              metrics=['accuracy'])

model.summary()
batch_size = 16

print("success")
