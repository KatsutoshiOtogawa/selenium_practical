package com.example;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.ArrayList;
// import java.util.List;
// import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/**
 * interfaceに変更。createAffiliateInfo createAffliateという風にする。
 * Model部分に何をダウンロードするかも決めるdowlonadFileという形で決める。
 */
public class DLSiteModel<T> extends Model<T> {

    public DLSiteModel()
    {
        this.TableName = "ArtCollection";
        this.ShopName = "DLSite";

        this.Items = new HashMap<String,String>();
        this.ListItems = new HashMap<String,Set<String>>();
        this.BoolItems = new HashMap<String,Boolean>();
        this.DBDependenItems = new HashMap<String,Map<String,T>>();

        constructor();
    }

    protected void constructor()
    {
        createShopItemInfoModel();
        createShopBasicInfo();
        createShopItemAffiliateInfoModel();
    }

    public void clear()
    {
        constructor();
    }

    public void createShopItemInfoModel()
    {

        Set<String> IlustratorName = new HashSet<String>();
        Set<String> VoiceActor = new HashSet<String>();
        Map<String,T> RerationMatome = new HashMap<String,T>();
        Set<String> Genru = new HashSet<String>();
        Map<String,T> MostProperyGenru = new HashMap<String,T>();
        Map<String,T> BuyingUserViewItems = new HashMap<String,T>();
        Map<String,T> LookingUserViewItems = new HashMap<String,T>();
        Set<String> FileFormat = new HashSet<String>();
        Set<String> Reviews = new HashSet<String>();
        Set<String> ScreenWriter = new HashSet<String>();
        Set<String> ItemCategory = new HashSet<String>();
        Set<String> Musician = new HashSet<String>();
        Set<String> Gallery = new HashSet<String>();
        
        Items.put("ShopItemId", "");
        Items.put("MakerName", "");
        Items.put("MakerFollowerNum", "");
        Items.put("UnitsSold", "");
        Items.put("SalePrice", "");
        Items.put("DiscountRate", "");
        Items.put("UntilHavingSale", "");
        Items.put("NormalPrice", "");
        Items.put("Assessment", "");
        Items.put("AssessmentNum", "");
        Items.put("MatomeNum", "");
        Items.put("ReleaseDate", "");
        Items.put("FileSize", "");
        Items.put("FileSizeUnit", "");
        Items.put("StarNum", "");
        Items.put("AgeVeridation", "");
        Items.put("ReviewNum", "");

        ListItems.put("IlustratorName", IlustratorName);
        ListItems.put("ItemCategory", ItemCategory);
        ListItems.put("FileFormat", FileFormat);
        ListItems.put("VoiceActor", VoiceActor);
        ListItems.put("Musician", Musician);
        ListItems.put("ScreenWriter", ScreenWriter);
        ListItems.put("Genru", Genru);
        ListItems.put("Gallery", Gallery);
        ListItems.put("Reviews", Reviews);

        BoolItems.put("Monopoly", false);

        DBDependenItems.put("RerationMatome", RerationMatome);
        DBDependenItems.put("MostProperyGenru", MostProperyGenru);
        DBDependenItems.put("BuyingUserViewItems", BuyingUserViewItems);
        DBDependenItems.put("LookingUserViewItems", LookingUserViewItems);

    }
    
    public void createShopBasicInfo()
    {

        Items.put("CreatedAt", "");
        Items.put("ItemName", "");
        Items.put("ShopName", "");
        Items.put("ShopItemName", "");

    }

    public void createShopItemAffiliateInfoModel()
    {

        Set<String> PlayerEmbed = new HashSet<String>();

        Items.put("AffiliateUrl", "");
        Items.put("AffiliateBigImageUrl", "");
        Items.put("AffiliateMiddleImageUrl", "");
        Items.put("AffiliateSmallImageUrl", "");

        ListItems.put("PlayerEmbed", PlayerEmbed);

    }

    public Map<String,String> createDowlonadFileModel()
    {

        return new HashMap<String,String>(){{
            put("Gallery","List");
            put("AffiliateBigImageUrl","");
            put("AffiliateMiddleImageUrl","");
            put("AffiliateSmallImageUrl","");
        }};
    }

    public void destructor()
    {

    }
}
