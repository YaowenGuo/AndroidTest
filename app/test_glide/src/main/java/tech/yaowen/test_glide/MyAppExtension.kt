package tech.yaowen.test_glide

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.annotation.GlideType
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions


@GlideExtension
public class MyAppExtension private constructor() {
    // Size of mini thumb in pixels.

    companion object {
        @JvmStatic
        private val MINI_THUMB_SIZE = 100
        private val DECODE_TYPE_GIF: RequestOptions =
            RequestOptions.decodeTypeOf(GifDrawable::class.java)
                .lock()


        @JvmStatic
        @GlideOption
        fun miniThumb(options: BaseRequestOptions<*>): BaseRequestOptions<*>? {
            return options
                .fitCenter()
                .override(MINI_THUMB_SIZE)
        }


        @JvmStatic
        @GlideType(GifDrawable::class)
        fun asMyGif(requestBuilder: RequestBuilder<GifDrawable?>): RequestBuilder<GifDrawable?> {
            return requestBuilder
                .transition(DrawableTransitionOptions())
                .apply(DECODE_TYPE_GIF)
        }

    }


}