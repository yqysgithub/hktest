import com.sun.jna.NativeLong;

import java.util.logging.Logger;

public class CameraManager {
//    private static Logger logger = LoggerFactory.getLogger(CameraManager.class);
    private static HCNetSDK sdk = HCNetSDK.INSTANCE;
    private static NativeLong userId = new NativeLong(-1);//用户登录ID，值为0,1,2...
    private static NativeLong startChan = new NativeLong(-1); // start channel number

    private void login(String ip, short port, String username, String pwd){
        //sdk初始化
        if (!sdk.NET_DVR_Init()){
            System.out.println("海康SDK初始化失败!" + sdk.NET_DVR_GetLastError());
        }
        //注册设备
        HCNetSDK.NET_DVR_DEVICEINFO_V30 deviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        //登录
        userId = sdk.NET_DVR_Login_V30(ip, port, username, pwd, deviceInfo);
        if (userId.intValue() < 0){
            System.out.println("摄像头用户登录失败!Err:" + sdk.NET_DVR_GetLastError());
        }
        startChan.setValue(36);//deviceInfo.byStartChan);
    }

    public boolean takePic(String ip, short port, String username, String pwd) {
        if (userId.intValue() < 0 || startChan.intValue() < 0) {
            System.out.println(String.format("执行海康摄像机登录，ip[{}],port[{}],username[{}].",ip,port,username));
            login(ip,port,username,pwd);
        }
        System.out.println(String.format("准备拍照，userId:[{}],startChan:[{}]",userId.intValue(),startChan.intValue()));
        //拍照
        HCNetSDK.NET_DVR_JPEGPARA strJpeg = new HCNetSDK.NET_DVR_JPEGPARA();
        strJpeg.wPicQuality = 1; //图像参数
        strJpeg.wPicSize = 2;

        String filePath = "d:\\123q.jpg";
        boolean b = sdk.NET_DVR_CaptureJPEGPicture(userId, new NativeLong(33), strJpeg, filePath);//尝试用NET_DVR_CaptureJPEGPicture_NEW方法，但不是报43就是JDK崩溃....
        if(!b){//单帧数据捕获图片
            System.out.println("抓拍失败!" + " err: " + sdk.NET_DVR_GetLastError());
        }
        else{
            System.out.println("抓拍成功");
        }
        return b;
    }

    public void logout(){
        sdk.NET_DVR_Logout(userId);
        sdk.NET_DVR_Cleanup();
    }

    public static void main(String[] args) {
        CameraManager manager = new CameraManager();
        manager.takePic("111.160.146.174",(short)8140,"admin","tjgy12345");
        manager.logout();
    }
}
