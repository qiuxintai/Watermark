#include <jni.h>
#include <string>
#include <jni.h>
 
static inline float byteToFloat255(uint8_t b) {
    return b >= 0 ? (float) b : b + 256.0f;
}
 
static inline uint8_t float255ToByte(float f) {
    if (f > 255) f = 255;
    else if (f < 0) f = 0;
    return (uint8_t) f;
}

extern "C" JNIEXPORT void JNICALL
Java_com_qxt_watermark_WatermarkUtils_add(JNIEnv *env, jclass clazz, jbyteArray src, jint srcWidth,
                   jint srcHeight, jbyteArray watermark, jint watermarkWidth, jint watermarkHeight,
                   jint x, jint y) {
    uint8_t *_src = (uint8_t *) env->GetByteArrayElements(src, nullptr);
    uint8_t *_watermark = (uint8_t *) env->GetByteArrayElements(watermark, nullptr);

    int positionX;
    if (x < 0) {
        positionX = 0;
    } else if (x > srcWidth - watermarkWidth) {
        positionX = srcWidth - watermarkWidth;
    } else {
        positionX = x;
    }
    int positionY;
    if (y < 0) {
        positionY = 0;
    } else if (y > srcHeight - watermarkHeight) {
        positionY = srcHeight - watermarkHeight;
    } else {
        positionY = y;
    }

    for (int h = 0; h < watermarkHeight; h++) {
        for (int w = 0; w < watermarkWidth; w++) {
            /*int srcPosition = ((srcHeight - watermarkHeight + y) * srcWidth
                               + srcWidth - watermarkWidth + x) * 4;*/
            int srcPosition = ((positionY + h) * srcWidth
                               + positionX + w) * 4;
            int position = (h * watermarkWidth + w) * 4;

            float watermarkAlpha = _watermark[position + 3] / 255.0f;
            float dstR = _src[srcPosition] * (1.0f - watermarkAlpha)
                         + _watermark[position] * watermarkAlpha;
            _src[srcPosition] = (uint8_t) dstR;
            float dstG = _src[srcPosition + 1] * (1.0f - watermarkAlpha)
                         + _watermark[position + 1] * watermarkAlpha;
            _src[srcPosition + 1] = (uint8_t) dstG;
            float dstB = _src[srcPosition + 2] * (1.0f - watermarkAlpha)
                         + _watermark[position + 2] * watermarkAlpha;
            _src[srcPosition + 2] = (uint8_t) dstB;
            float dstA = _src[srcPosition + 3] * (1.0f - watermarkAlpha)
                         + _watermark[position + 3] * watermarkAlpha;
            _src[srcPosition + 3] = (uint8_t) dstA;
        }
    }
    env->ReleaseByteArrayElements(watermark, (jbyte *) _watermark, JNI_ABORT);
    env->ReleaseByteArrayElements(src, (jbyte *) _src, 0);
}