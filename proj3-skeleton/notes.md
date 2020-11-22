### Server
mc18.cs.purdue.edu
cuda.cs.purdue.edu (for GPU)

### Set up environments
source /homes/cs510/project-3/venv/bin/activate

module load cuda/10.0
source /homes/cs510/project-3/venv-gpu/bin/activate (for GPU)
nvidia-smi (check available GPUs)
export CUDA_VISIBLE_DEVICES=1  # replace 1 with the id of the gpu

### Use screen
run  `screen` in terminal
run the python script
ctrl-A, then ctrl-D to exit `screen`
next time log in, run screen -r to go back to previous process
