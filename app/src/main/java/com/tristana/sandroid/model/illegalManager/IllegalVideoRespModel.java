package com.tristana.sandroid.model.illegalManager;

import java.util.List;

public class IllegalVideoRespModel {
    /**
     * code : 0
     * data : [{"cover":"https://storage.tracup.com/o_1ejprhgik1k3u1fp3div2agcpsb.jpeg","file":"https://storage.tracup.com/o_1ejpb0d8i1ive10rngdo1ckr1g2518.mp4","content":"视频1"},{"cover":"https://storage.tracup.com/o_1ejprhgik1f2gilc1u882nd93mc.jpeg","file":"https://storage.tracup.com/o_1ejpb0d8i86e4r9sf518vc3l119.mp4","content":"视频2"},{"cover":"https://storage.tracup.com/o_1ejpjn2hr1m1q4ep182f42th4a.jpeg","file":"https://storage.tracup.com/o_1ejpjnhn81n2a40ic1f4l1u9ef.mp4","content":"视频3"},{"cover":"https://storage.tracup.com/o_1ejpbjg0v1lo610dp9v6181ue24.jpeg","file":"https://storage.tracup.com/o_1ejpb0d8i1meg7njpiafs8qps1b.mp4","content":"视频4"}]
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
         * cover : https://storage.tracup.com/o_1ejprhgik1k3u1fp3div2agcpsb.jpeg
         * file : https://storage.tracup.com/o_1ejpb0d8i1ive10rngdo1ckr1g2518.mp4
         * content : 视频1
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
