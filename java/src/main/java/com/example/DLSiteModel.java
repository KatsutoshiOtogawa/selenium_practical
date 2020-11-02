package com.example;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
/**
 * interfaceに変更。createAffiliateInfo createAffliateという風にする。
 * Model部分に何をダウンロードするかも決めるdowlonadFileという形で決める。
 */
public class DLSiteModel extends Model {

    public Map<String,Object> createShopItemInfoModel()
    {
        Set<String> IlustratorName = new HashSet<String>();
        Set<String> VoiceActor = new HashSet<String>();
        Map<String,AttributeValue> RerationMatome = new HashMap<String,AttributeValue>();
        Set<String> Genru = new HashSet<String>();
        Map<String,AttributeValue> MostProperyGenru = new HashMap<String,AttributeValue>();
        Map<String,AttributeValue> BuyingUserViewItems = new HashMap<String,AttributeValue>();
        Map<String,AttributeValue> LookingUserViewItems = new HashMap<String,AttributeValue>();
        Set<String> FileFormat = new HashSet<String>();
        Set<String> Reviews = new HashSet<String>();
        Set<String> ScreenWriter = new HashSet<String>();
        Set<String> ItemCategory = new HashSet<String>();
        Set<String> Musician = new HashSet<String>();
        Set<String> Gallery = new HashSet<String>();
        
        return new HashMap<String,Object>(){{
            put("ShopArtId", "");
            put("MakerName", "");
            put("MakerFollowerNum", "");
            put("UnitsSold", "");
            put("SalePrice", "");
            put("DiscountRate", "");
            put("UntilHavingSale", "");
            put("NormalPrice", "");
            put("Assessment", "");
            put("AssessmentNum", "");
            put("IlustratorName", IlustratorName);
            put("MatomeNum", "");
            put("RerationMatome", RerationMatome);
            put("ItemCategory", ItemCategory);
            put("ReleaseDate", "");
            put("FileFormat", FileFormat);
            put("FileSize", "");
            put("FileSizeUnit", "");
            put("StarNum", "");
            put("AgeVeridation", "");
            put("VoiceActor", VoiceActor);
            put("Musician", Musician);
            put("ScreenWriter", ScreenWriter);
            put("Genru", Genru);
            put("MostProperyGenru", MostProperyGenru);
            put("Gallery", Gallery);
            put("BuyingUserViewItems", BuyingUserViewItems);
            put("LookingUserViewItems", LookingUserViewItems);
            put("ReviewNum", "");
            put("Reviews", Reviews);
            put("Monopoly", false);
        }};
    }

    public Map<String,Object> createShopItemAffiliateInfoModel()
    {
        Set<String> PlayerEmbed = new HashSet<String>();

        return new HashMap<String,Object>(){{
            put("AffiliateUrl", "");
            put("AffiliateBigImageUrl", "");
            put("AffiliateMiddleImageUrl", "");
            put("AffiliateSmallImageUrl", "");
            put("PlayerEmbed", PlayerEmbed);
        }};
    }

    public List<String> createDowlonadFileModel()
    {

        return new ArrayList<String>(
            Arrays.asList(
                "Gallery"
            )
        );
    }

    // public Map<String,Object> createFetchScrapingModel()
    // {
    //     return new HashMap<String,Object>(){{
    //     }};
    // }

    public void destructor()
    {

    }
}
