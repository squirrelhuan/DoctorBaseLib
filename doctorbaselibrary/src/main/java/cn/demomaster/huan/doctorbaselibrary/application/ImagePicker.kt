package yibai.com.yidao.util

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.UpCompletionHandler
import org.json.JSONObject
import java.io.File

/**
 * Created by zhuozhongcao on 2018/6/26.
 */

object ImagePicker {

    private var selectList: List<LocalMedia>? = null
    private var urlCallback: StringCallback? = null

    /**
     * 自定义压缩存储地址
     *
     * @return
     */
    private fun getPath(): String {
        val path = Environment.getExternalStorageDirectory().toString() + "/Luban/image/"
        val file = File(path)
        return if (file.mkdirs()) {
            path
        } else path
    }

    fun callImageSelect(activity: Activity, crop: Boolean = false, callback: StringCallback) {
        urlCallback = callback

        selectList = mutableListOf()
        PictureSelector.create(activity)
                //全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .openGallery(PictureMimeType.ofImage())
                // 最大图片选择数量 int
                .maxSelectNum(1)
                // 最小选择数量 int
                .minSelectNum(0)
                // 每行显示个数 int
                .imageSpanCount(4)
                // 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .selectionMode(PictureConfig.SINGLE)
                // 是否可预览图片 true or false
                .previewVideo(false)
                // 是否可预览视频 true or false
                .previewImage(true)
                // 是否可播放音频 true or false
                .enablePreviewAudio(false)
                // 是否显示拍照按钮 true or false
                .isCamera(true)
                // 拍照保存图片格式后缀,默认jpeg
                .imageFormat(PictureMimeType.PNG)
                // 图片列表点击 缩放效果 默认true
                .isZoomAnim(true)
                // glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .sizeMultiplier(0.5f)
                // 自定义拍照保存路径,可不填
//                .setOutputCameraPath("/CustomPath")
                // 是否裁剪 true or false
                .enableCrop(crop)
                // 是否压缩 true or false
                .compress(true)
                // int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .glideOverride(160, 160)
                // int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .withAspectRatio(1, 1)
                // 是否显示uCrop工具栏，默认不显示 true or false
                .hideBottomControls(false)
                // 是否显示gif图片 true or false
                .isGif(true)
                //压缩图片保存地址
                .compressSavePath(getPath())
                // 裁剪框是否可拖拽 true or false
                .freeStyleCropEnabled(true)
                // 是否圆形裁剪 true or false
                .circleDimmedLayer(false)
                // 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropFrame(false)
                // 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .showCropGrid(false)
                // 是否开启点击声音 true or false
                .openClickSound(false)
                // 是否传入已选图片
                .selectionMedia(selectList)
                // 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .previewEggs(true)
                // 裁剪压缩质量 默认90 int
                .cropCompressQuality(90)
                // 小于100kb的图片不压缩
                .minimumCompressSize(100)
                //同步true或异步false 压缩 默认同步
                .synOrAsy(true)
                // 裁剪宽高比，设置如果大于图片本身宽高则无效 int
                //                .cropWH()
                // 裁剪是否可旋转图片 true or false
                .rotateEnabled(true)
                // 裁剪是否可放大缩小图片 true or false
                .scaleEnabled(true)
                //结果回调onActivityResult code
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    fun onImagePickerFinish(data: Intent) {
        // 图片选择结果回调
        selectList = PictureSelector.obtainMultipleResult(data)
        for (media in selectList!!) {
            Log.i("图片-----》", media.path)
        }
        uploadImage()
    }

    private fun uploadImage() {
        QiniuUtils.uploadImage(selectList!![0].compressPath, UpCompletionHandler { key, info, response ->
            info?.isOK?.let {
                urlCallback?.invoke(QiniuUtils.urlByKey(response.getString("key")))
                urlCallback = null
            }
        })
    }
}