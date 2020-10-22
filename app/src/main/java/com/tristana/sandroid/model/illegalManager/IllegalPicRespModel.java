package com.tristana.sandroid.model.illegalManager;

import java.util.List;

public class IllegalPicRespModel {
    /**
     * code : 0
     * data : [{"cover":"https://storage.tracup.com/o_1ejra11kj8v56sug641dar1jqgd.jpeg","file":"","content":"图片1"},{"cover":"https://storage.tracup.com/o_1ejra11kj1fbf125l1eqrvqr1ggue.jpeg","file":"","content":"图片2"},{"cover":"https://storage.tracup.com/o_1ejra11kjt4i1p5sg86brb1o4vf.jpeg","file":"","content":"图片3"},{"cover":"https://storage.tracup.com/o_1ejra11kj1thd1s3q124f11og14prg.jpeg","file":"","content":"图片4"}]
     */

    private String code;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * cover : https://storage.tracup.com/o_1ejra11kj8v56sug641dar1jqgd.jpeg
         * file :
         * content : 图片1
         */

        private String cover;
        private String file;
        private String content;

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
