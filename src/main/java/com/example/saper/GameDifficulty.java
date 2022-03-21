package com.example.saper;

public enum GameDifficulty {
    Easy{
        public Config GetConfigField(){
            Config config = new Config(100, 50, "Easy");
            config.setCountMines(16);
            return  config;
        }
    },
    Normal{
        public Config GetConfigField() { return null; }
    },
    Hard{
        public Config GetConfigField() { return null; }
    };

    public abstract Config GetConfigField();
}
