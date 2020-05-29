package top.shanwer;

import java.io.IOException;
import java.net.*;

public class WakeOnLan {
    /**
     * 虽然使用的是UDP协议，但是TCP也是可以的，只要数据包内包含的信息匹配就行了
     * MagicPacket正统译名叫魔术数据包，翻译成魔法数据包也差不多嘛，此程序因而得名S_Wand(Shanwer的魔法棒)
     * 参照了博客:https://blog.csdn.net/u013363811/article/details/44343437/
     * 命令行参数用来自定义主机地址，MAC地址，端口
     * 比如 java -jar S_Wand test.top AA-BB-CC-DD-EE-FF 9，MAC加':"也行，但形如AABBCCDDEEFF是不可以的
     */
    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getByName(args[0]);//域名地址，IP地址也是可以的
            String ip = address.getHostAddress();//解析域名(IP)到IP
            String MAC = args[1];//MAC地址的原始地址，需要加入":"与"-"
            //话说String MAC = args[1].replaceAll("(?::|-)","");和上面一行居然是等效的，这也许就是正则表达式吧,i了i了
            String port = args[2];//端口号
            //将MAC地址转换为2进制
            byte[] MACTo2Bits = getMacBytes(MAC);
            try {
                byte[] magic = new byte[102];
                for (int i = 0; i < 6; i++)
                    magic[i] = (byte) 0xFF;
                //从第7个位置开始把MAC地址放入16次
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < MACTo2Bits.length; j++) {
                        magic[6 + MACTo2Bits.length * i + j] = MACTo2Bits[j];
                    }
                }
                DatagramPacket packet = new DatagramPacket(magic, magic.length, InetAddress.getByName(ip), Integer.parseInt(port));
                //创建套接字
                DatagramSocket socket = new DatagramSocket();
                //发送数据
                socket.send(packet);
                //关闭socket
                socket.close();
                /*
                System.out.println("[DEBUG]发送的数据包内容");
                for (byte s : magic) {
                    System.out.print(s);
                }
                */
                System.out.println("\n向地址:" + InetAddress.getByName(ip) + ",端口:" + Integer.parseInt(port) + ",发送了魔法数据包");
            } catch (IOException e) {
                //获取socket失败时候抛出的异常
                e.printStackTrace();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            //一般是命令行参数不全导致的
            e.printStackTrace();
            for (String errorString : args) {
                System.out.println("你输入的命令行参数有误，示例:java -jar S_Wand test.top AA-BB-CC-DD-EE-FF 9,你输入的命令行参数为:" + errorString);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("域名解析失败或IP地址有误，你输入的为:" + args[0]);
        }
    }
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        //MAC地址转换二进制方法
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("([:\\-])");
        if (hex.length != 6) {
            throw new IllegalArgumentException("无效的MAC地址");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的MAC地址");
        }
        return bytes;
    }
}