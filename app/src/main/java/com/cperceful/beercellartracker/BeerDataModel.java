package com.cperceful.beercellartracker;


import org.json.JSONException;
import org.json.JSONObject;

public class BeerDataModel {

    private String brewery;
    private String name;
    private String abv;
    private String style;

    public static BeerDataModel fromJson(JSONObject jsonObject){
        try {
            BeerDataModel beerDataModel = new BeerDataModel();
            beerDataModel.name = jsonObject.getJSONArray("data").getJSONObject(0).getString("name");
            beerDataModel.brewery = jsonObject.getJSONArray("data").getJSONObject(0)
                    .getJSONArray("breweries").getJSONObject(0).getString("name");
            beerDataModel.abv = jsonObject.getJSONArray("data")
                    .getJSONObject(0).getString("abv");
            beerDataModel.style = jsonObject.getJSONArray("data").getJSONObject(0)
                    .getJSONObject("style").getString("name");

            return beerDataModel;
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public String getBrewery() {
        return brewery;
    }

    public String getName() {
        return name;
    }

    public String getAbv() {
        return abv;
    }

    public String getStyle() {
        return style;
    }
}
