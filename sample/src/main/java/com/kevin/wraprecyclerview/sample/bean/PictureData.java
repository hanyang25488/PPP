package com.kevin.wraprecyclerview.sample.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhouwk on 2015/12/25 0025.
 */
public class PictureData implements Serializable{

    public List<Picture> list;

    public class Picture implements Serializable {
        public String IMAGE_URL;
    }

}
