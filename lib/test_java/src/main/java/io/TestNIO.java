package io;

import java.nio.ByteBuffer;

public class TestNIO {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        System.out.println("--------写入数据之前-------");
        System.out.println("容量" + buffer.capacity());
        System.out.println("限制" + buffer.limit());
        System.out.println("位置" + buffer.position());

        System.out.println("--------写入数据之后-------");
        buffer.put("hello".getBytes());
        System.out.println("容量" + buffer.capacity());
        System.out.println("限制" + buffer.limit());
        System.out.println("位置" + buffer.position());

        System.out.println("-------转换为读取模式后-----");
        buffer.flip();
        System.out.println("容量" + buffer.capacity());
        System.out.println("限制" + buffer.limit());
        System.out.println("位置" + buffer.position());

        //读取
        System.out.println("-------读取之后-----");
        byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        System.out.println("容量" + buffer.capacity());
        System.out.println("限制" + buffer.limit());
        System.out.println("位置" + buffer.position());

        System.out.println("---rewind()重复读取--");
        buffer.rewind();
        System.out.println("容量" + buffer.capacity());
        System.out.println("限制" + buffer.limit());
        System.out.println("位置" + buffer.position());

        System.out.println("-----读取三个字节，但是没读完hello-----");
        byte b1 = buffer.get();
        byte b2 = buffer.get();
        byte b3 = buffer.get();//读取三个字节，但是没读完hello
        System.out.println("容量" + buffer.capacity());
        System.out.println("限制" + buffer.limit());
        System.out.println("位置" + buffer.position());

        System.out.println("----compact压缩，后面未读完的前移----");
        buffer.compact();
        System.out.println("容量" + buffer.capacity());
        System.out.println("限制" + buffer.limit());
        System.out.println("位置" + buffer.position());
        buffer.flip();
        System.out.println("flip后限制" + buffer.limit());
        byte b4 = buffer.get();
        byte b5 = buffer.get();
        System.out.println((char) b4);
        System.out.println((char) b5);


        //清空，只是把指针位置修改了，内容还在，可被覆盖
        System.out.println("------清空----------");
        buffer.clear();
        System.out.println("容量" + buffer.capacity());
        System.out.println("限制" + buffer.limit());
        System.out.println("位置" + buffer.position());
    }

}
