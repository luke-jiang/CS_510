import pickle
import pandas

print("opening data files")

with open('/homes/cs510/project-3/data/train.pickle', 'rb') as handle:
    train = pickle.load(handle)

length = len(train["instance"])

# print(train.columns)
# print(train["instance"].shape)
# print(train["instance"].values[0])

print("searching for duplicates")

instances = set()

clean_inst = []
clean_ctx_bf = []
clean_ctx_af = []
clean_flag = []

dup_count = 0
clean_count = 0

old_inst = train["instance"].values
old_ctx_bf = train["context_before"].values
old_ctx_af = train["context_after"].values
old_flag = train["is_buggy"].values

for i in range(0, length):
    inst = old_inst[i]
    ctx_bf = old_ctx_bf[i]
    ctx_af = old_ctx_af[i]
    flag = old_flag[i]

    key = inst + ctx_bf + ctx_af
    if key not in instances:
        instances.add(key)
        inst1 = inst.strip()
        if len(inst1) == 0 and inst1.startswith("{}();"):
            continue
        clean_inst.append(inst)
        clean_ctx_bf.append(ctx_bf)
        clean_ctx_af.append(ctx_af)
        clean_flag.append(flag)
        clean_count += 1
    else:
        dup_count += 1
        
clean_train = pandas.DataFrame({"instance": clean_inst, "context_before": clean_ctx_bf, "context_after": clean_ctx_af, "is_buggy": clean_flag})

print("dumping")

with open('data/cleaned_train.pickle', 'wb') as handle:
    pickle.dump(clean_train, handle, protocol=pickle.HIGHEST_PROTOCOL)

print("done. Found " + str(dup_count) + " duplicate instances")
print("clean instances: " + str(clean_count))
