package main.java.com.learn.serialize;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Java序列化的缺点
 * 1.无法跨语言
 * 2.序列化后的码流太大
 *
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private int userId;
    public UserInfo buildUserName(String userName) {
        this.userName = userName;
        return this;
    }
    public UserInfo buildUserInfo(int userId) {
        this.userId = userId;
        return this;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    public byte[] codeC() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userId);
        buffer.flip();
        value = null;
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }
}
