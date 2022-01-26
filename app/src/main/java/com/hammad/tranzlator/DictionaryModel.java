package com.hammad.tranzlator;

public class DictionaryModel {

    String partOfSpeech;
    String definition;
    String example;

     public DictionaryModel(String partOfSpeech, String definition, String example) {
          this.partOfSpeech = partOfSpeech;
          this.definition = definition;
          this.example = example;
     }

     public String getPartOfSpeech() {
          return partOfSpeech;
     }

     public void setPartOfSpeech(String partOfSpeech) {
          this.partOfSpeech = partOfSpeech;
     }

     public String getDefinition() {
          return definition;
     }

     public void setDefinition(String definition) {
          this.definition = definition;
     }

     public String getExample() {
          return example;
     }

     public void setExample(String example) {
          this.example = example;
     }
}
