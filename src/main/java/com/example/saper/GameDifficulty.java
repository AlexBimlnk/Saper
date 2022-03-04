package com.example.saper;

public enum GameDifficulty {
    Easy{
        public Config GetConfigField() { return new Config(100, 50); }
    },
    Normal{
        public Config GetConfigField() { return null; }
    },
    Hard{
        public Config GetConfigField() { return null; }
    };

    public  abstract Config GetConfigField();
}
