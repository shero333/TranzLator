package com.hammad.tranzlator;

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
          this.synonyms=synonyms;
     }

     public String getPartOfSpeech() {
          return partOfSpeech;
     }

     /*public void setPartOfSpeech(String partOfSpeech) {
          this.partOfSpeech = partOfSpeech;
     }*/

     public String getDefinition() {
          return definition;
     }

     /*public void setDefinition(String definition) {
          this.definition = definition;
     }*/

     public String getExample() {
          return example;
     }

     /*public void setExample(String example) {
          this.example = example;
     }*/

    /*public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }*/


}
