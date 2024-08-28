# AI VS Human Battleship Game

## Playground Image
![Line2Live Model Architecture](https://github.com/angel-gao/Line2Live/blob/main/projectModelFC.png)

## Introduction

**AI VS Human Battleship Game** AI vs. Human Battleship is an interactive game developed using Java and JavaFX, offering a graphical user interface for engaging battleship gameplay. Players can challenge an artificial intelligence in a classic battle of naval strategy. For the rules to play the game, please reference to [Link Text]([URL](https://www.hasbro.com/common/instruct/battleship.pdf))

## Features

- Designed a graphical user interface with JavaFX that supports basic gaming functionalities like text display, difficulty selection, instructions page, sound effects, background music, and saving and loading gaming features
- Boosted the AI’s winning rate against human users to about 60% by applying the concept of a probability density function


## File Descriptions
- **config.py**: setup the configurations and type of transformations for data-preprocessing for entire training
- **dataset.py & dataset_multi.py** customized datasets for loading multi images simultaneously
- **discriminator.py**: discriminator model class
- **generator.py**: generator model class
- **metric_evaluation**: evaluate the L1, L2 distance and SSIM based on images generated saved in Final_Generation folder with input test sketches
- **utils.py**: some helper functions for image saving and model loading

## Usage

To get started with Line2Live, follow these steps to set up the project on your local machine.

```bash
# Clone the repository
git clone https://github.com/angel-gao/Line2Live.git

# Install dependencies
conda env create -f environment.yml
```

To run the baseline model: 
```bash
#Remember to setup the desired configurations and correct dataset directory
python train_base.py
```

To run the project model: 
```bash
#Remember to setup the desired configurations and correct dataset directory
python train_triple.py
```



