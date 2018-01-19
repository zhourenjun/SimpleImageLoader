package com.lh.zrj.simpleimageloader

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.lh.imgloader.core.SimpleImageLoader
import com.lh.imgloader.policy.ReversePolicy
import com.lh.imgloader.cache.DoubleCache
import com.lh.imgloader.config.ImageLoaderConfig
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.image_item_layout.view.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initImageLoader()
        check()
    }

    private fun initImageLoader() {
        val config = ImageLoaderConfig()
                .setLoadingPlaceholder(R.drawable.loading)
                .setNotFoundPlaceholder(R.drawable.not_found)
                .setCache(DoubleCache(this))
                .setThreadCount(4)
                .setLoadPolicy(ReversePolicy())
        // 初始化
        SimpleImageLoader.instance.init(config)
    }

    private fun check() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        MPermissionUtils.requestPermissionsResult(this, 1, permissions, object : MPermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                gridview.adapter = ImageItemAdaper(imageThumbUrls)
            }

            override fun onPermissionDenied() {
                MPermissionUtils.showTipsDialog(this@MainActivity)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MPermissionUtils.settingRequestCode) {
            check()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        MPermissionUtils.onRequestPermissionsResult(requestCode, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        // 退出时关闭
        SimpleImageLoader.instance.stop()
        super.onDestroy()
    }

    private inner class ImageItemAdaper(datas: Array<String>) : ArrayAdapter<String>(this, 0, datas) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            @Suppress("NAME_SHADOWING")
            var convertView = convertView
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.image_item_layout, parent, false)
            }
            // 加载图片
            SimpleImageLoader.instance.displayImage(convertView!!.img, getItem(position))
            return convertView
        }
    }


    companion object {

        val imageThumbUrls = arrayOf(
                "https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=tu&step_word=&hs=2&pn=7&spn=0&di=60931554310&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=4181980489%2C1376456300&os=2856544822%2C2113939917&simid=3367461142%2C298490053&adpicid=0&lpn=0&ln=1969&fr=&fmq=1516271967810_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fp0.qhimg.com%2Ft01dca508dce5b5069b.png&fromurl=ippr_z2C%24qAzdH3FAzdH3F4_z%26e3B41r1w_z%26e3Bv54AzdH3FwrrAzdH3Fwrhd8mm0ll_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0",
                "https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=tu&step_word=&hs=2&pn=8&spn=0&di=63914860370&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=1043572920%2C3425200009&os=1083011866%2C3238446772&simid=0%2C0&adpicid=0&lpn=0&ln=1969&fr=&fmq=1516271967810_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=13&oriquery=&objurl=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%3Dshijue1%2C0%2C0%2C294%2C40%2Fsign%3D5022a7eab8119313d34ef7f30d5166a2%2Fb17eca8065380cd79242cbc5ab44ad34598281bd.jpg&fromurl=ipprf_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bev2_z%26e3Bv54AzdH3Fv6jwptejAzdH3Fb8n8dda89&gsm=0&rpstart=0&rpnum=0",
                "https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=tu&step_word=&hs=2&pn=9&spn=0&di=170164659060&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2587929985%2C1287731020&os=415055029%2C1336714550&simid=3438389861%2C482270164&adpicid=0&lpn=0&ln=1969&fr=&fmq=1516271967810_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fcimage1.tianjimedia.com%2FuploadImages%2F2017%2F01%2F20170123144932788.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fgjp_z%26e3Bvitgwkypj_z%26e3Bv54AzdH3FnmbAzdH3F89a9cnmb_z%26e3Bfip4s&gsm=0&rpstart=0&rpnum=0",
                "https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=tu&step_word=&hs=2&pn=10&spn=0&di=117738080680&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3532638848%2C1603047958&os=2102484605%2C3090076833&simid=3485360510%2C468846398&adpicid=0&lpn=0&ln=1969&fr=&fmq=1516271967810_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fwww.australia.com%2Fcontent%2Faustralia%2Fid_id%2Fplaces%2Fgreat-ocean-road%2Fexperiences-attractions%2F_jcr_content%2Fimage.adapt.585.medium.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bw7fp6wstw_z%26e3Bv54AzdH3Ft1-t1AzdH3FrswvjfAzdH3F26jwp-5vjwg-65w1_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0",
                "https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=tu&step_word=&hs=2&pn=11&spn=0&di=123820806010&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3885823919%2C3995537631&os=1809684110%2C1106807452&simid=0%2C0&adpicid=0&lpn=0&ln=1969&fr=&fmq=1516271967810_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fimg2.ph.126.net%2FIq0sLJ9qOm5ubIAAUiLXIA%3D%3D%2F1050464613102373775.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fks52_z%26e3B8mn_z%26e3Bv54AzdH3F48cnlldddnca_9AzdH3Fks52AzdH3FfpwptvAzdH3Fdcacb0al9da8cb8al98ccdc9AzdH3F&gsm=0&rpstart=0&rpnum=0",
                "https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=tu&step_word=&hs=2&pn=12&spn=0&di=156598844050&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=4113935051%2C664227799&os=2445543236%2C3086412186&simid=4203655236%2C752083949&adpicid=0&lpn=0&ln=1969&fr=&fmq=1516271967810_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fphoto20.hexun.com.tw%2Fp%2F2011%2F1020%2F457439%2Fb_vip_80ECA3EFF3C7232088656B13389BB1F2.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fndl0acb_z%26e3Bks52_z%26e3Bijx7g_z%26e3Bv54_z%26e3BpoAzdH3F0dbc8md9_1_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0",
                "https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=tu&step_word=&hs=2&pn=13&spn=0&di=164227821780&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=1306717043%2C2610336008&os=1358202518%2C4281441141&simid=4099043104%2C690954164&adpicid=0&lpn=0&ln=1969&fr=&fmq=1516271967810_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fs2.sinaimg.cn%2Fmw690%2F006wmg2Hzy771ypDaWl21%26690&fromurl=ippr_z2C%24qAzdH3FAzdH3Fks52_z%26e3Bftgw_z%26e3Bv54_z%26e3BvgAzdH3FfAzdH3Fks52_8m9ddmk00a8adoglj_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0",
                // 以下三张为本地图片,本地图片支持uri格式，形如file:// + 图片绝对路径
                "file:///storage/emulated/0/Camera/P41115-140216.jpg",
                "file:///storage/emulated/0/Camera/P41115-142950.jpg",
                "file:///storage/emulated/0/Camera/P50102-133614.jpg",
                "http://img.my.csdn.net/uploads/201407/26/not_found_haha.jpg"// 不存在的图片
        )
    }
}
