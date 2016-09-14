package com.example.spj.mobileplayer.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 作用：xxxx
 */
public class SearchBean implements Parcelable{


    private String flag;
    private String pageNo;
    private String pageSize;
    private String wd;
    private String total;
    /**
     * itemID : ARTIR2By66MeqhVrfksnmeDs160912
     * itemTitle : 《长征故事》：主力红军为何能顺利渡过于都河
     * itemType : article_flag
     * detailUrl : http://app.cntv.cn/special/news/detail/arti/index.html?id=ARTIR2By66MeqhVrfksnmeDs160912&amp;isfromapp=1
     * pubTime : 2016-09-12 10:20:40
     * keywords : 《长征故事》：主力红军为何能顺利渡过于都河
     * category : 最新
     * guid :
     * videoLength :
     * source : 央视新闻客户端
     * brief :
     * photoCount : 0
     * sub_column_id : PAGE1373939846580285
     * datecheck : 2016-09-12
     * itemImage : {"imgUrl1":"http://p1.img.cctvpic.com/photoworkspace/2016/09/12/2016091210051822314.png"}
     */

    private List<ItemsEntity> items;

    protected SearchBean(Parcel in) {
        flag = in.readString();
        pageNo = in.readString();
        pageSize = in.readString();
        wd = in.readString();
        total = in.readString();
    }

    public static final Creator<SearchBean> CREATOR = new Creator<SearchBean>() {
        @Override
        public SearchBean createFromParcel(Parcel in) {
            return new SearchBean(in);
        }

        @Override
        public SearchBean[] newArray(int size) {
            return new SearchBean[size];
        }
    };

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public void setWd(String wd) {
        this.wd = wd;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setItems(List<ItemsEntity> items) {
        this.items = items;
    }

    public String getFlag() {
        return flag;
    }

    public String getPageNo() {
        return pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public String getWd() {
        return wd;
    }

    public String getTotal() {
        return total;
    }

    public List<ItemsEntity> getItems() {
        return items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(flag);
        parcel.writeString(pageNo);
        parcel.writeString(pageSize);
        parcel.writeString(wd);
        parcel.writeString(total);
    }

    public static class ItemsEntity implements Parcelable{
        private String itemID;
        private String itemTitle;
        private String itemType;
        private String detailUrl;
        private String pubTime;
        private String keywords;
        private String category;
        private String guid;
        private String videoLength;
        private String source;
        private String brief;
        private String photoCount;
        private String sub_column_id;
        private String datecheck;
        /**
         * imgUrl1 : http://p1.img.cctvpic.com/photoworkspace/2016/09/12/2016091210051822314.png
         */

        private ItemImageEntity itemImage;

        protected ItemsEntity(Parcel in) {
            itemID = in.readString();
            itemTitle = in.readString();
            itemType = in.readString();
            detailUrl = in.readString();
            pubTime = in.readString();
            keywords = in.readString();
            category = in.readString();
            guid = in.readString();
            videoLength = in.readString();
            source = in.readString();
            brief = in.readString();
            photoCount = in.readString();
            sub_column_id = in.readString();
            datecheck = in.readString();
        }

        public static final Creator<ItemsEntity> CREATOR = new Creator<ItemsEntity>() {
            @Override
            public ItemsEntity createFromParcel(Parcel in) {
                return new ItemsEntity(in);
            }

            @Override
            public ItemsEntity[] newArray(int size) {
                return new ItemsEntity[size];
            }
        };

        public void setItemID(String itemID) {
            this.itemID = itemID;
        }

        public void setItemTitle(String itemTitle) {
            this.itemTitle = itemTitle;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public void setDetailUrl(String detailUrl) {
            this.detailUrl = detailUrl;
        }

        public void setPubTime(String pubTime) {
            this.pubTime = pubTime;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        public void setVideoLength(String videoLength) {
            this.videoLength = videoLength;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public void setBrief(String brief) {
            this.brief = brief;
        }

        public void setPhotoCount(String photoCount) {
            this.photoCount = photoCount;
        }

        public void setSub_column_id(String sub_column_id) {
            this.sub_column_id = sub_column_id;
        }

        public void setDatecheck(String datecheck) {
            this.datecheck = datecheck;
        }

        public void setItemImage(ItemImageEntity itemImage) {
            this.itemImage = itemImage;
        }

        public String getItemID() {
            return itemID;
        }

        public String getItemTitle() {
            return itemTitle;
        }

        public String getItemType() {
            return itemType;
        }

        public String getDetailUrl() {
            return detailUrl;
        }

        public String getPubTime() {
            return pubTime;
        }

        public String getKeywords() {
            return keywords;
        }

        public String getCategory() {
            return category;
        }

        public String getGuid() {
            return guid;
        }

        public String getVideoLength() {
            return videoLength;
        }

        public String getSource() {
            return source;
        }

        public String getBrief() {
            return brief;
        }

        public String getPhotoCount() {
            return photoCount;
        }

        public String getSub_column_id() {
            return sub_column_id;
        }

        public String getDatecheck() {
            return datecheck;
        }

        public ItemImageEntity getItemImage() {
            return itemImage;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(itemID);
            parcel.writeString(itemTitle);
            parcel.writeString(itemType);
            parcel.writeString(detailUrl);
            parcel.writeString(pubTime);
            parcel.writeString(keywords);
            parcel.writeString(category);
            parcel.writeString(guid);
            parcel.writeString(videoLength);
            parcel.writeString(source);
            parcel.writeString(brief);
            parcel.writeString(photoCount);
            parcel.writeString(sub_column_id);
            parcel.writeString(datecheck);
        }

        public static class ItemImageEntity {
            private String imgUrl1;

            public void setImgUrl1(String imgUrl1) {
                this.imgUrl1 = imgUrl1;
            }

            public String getImgUrl1() {
                return imgUrl1;
            }
        }
    }
}
