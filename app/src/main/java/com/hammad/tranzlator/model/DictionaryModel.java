package com.hammad.tranzlator.model;

import java.util.List;

public class DictionaryModel {

    String partOfSpeech;
    String definition;
    String example;
    public List<String> synonyms;


    public DictionaryModel(String partOfSpeech, String definition, String example, List<String> synonyms) {
        this.partOfSpeech = partOfSpeech;
        this.definition = definition;
        this.example = example;
        this.synonyms = synonyms;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }


    public String getDefinition() {
        return definition;
    }


    public String getExample() {
        return example;
    }


}
