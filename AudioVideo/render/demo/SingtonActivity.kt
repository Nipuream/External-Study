package com.example.dhdemo.pages

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.dahua.dhcontrolcenter.render.KeyBoardRender
import com.example.dhdemo.R
import com.example.dhdemo.widgets.MySurfaceView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import java.lang.RuntimeException

/**
 * author  :  Nipuream
 * date    :  2021/3/2
 * email   :  nipuream@163.com
 * describe:
 */
class SingtonActivity : AppCompatActivity() {


    companion object {

        @JvmStatic
        private val FILES_DIR  = Environment.getExternalStorageDirectory()
        @JvmStatic
        private val INPUT_FILE = "4k.mp4"
    }


    var surfaceView : MySurfaceView ?= null
    lateinit var render : KeyBoardRender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singleton)
        surfaceView = findViewById(R.id.surfaceview)
    }

    fun filp(view: View) {

        println("width : ${surfaceView?.width}, height : ${surfaceView?.height}")


        //这三部一定要同一个线程
        GlobalScope.launch (Dispatchers.Default){
            render = KeyBoardRender(surfaceView?.getSurface(), surfaceView!!.width, surfaceView!!.height)
//            render.native_init(surfaceView?.getSurface(), surfaceView!!.width, surfaceView!!.height)
//            render.setup()
            extractMepgFrames()
        }

        //ExtractMpegFramesTest ...
//        ExtractMpegFramesTest().testExtractMpegFrames(surfaceView?.getSurface())

    }


    private fun extractMepgFrames(){

        var decoder : MediaCodec ?= null
        var extractor : MediaExtractor ?= null

        try {
            val inputFile = File(FILES_DIR, INPUT_FILE)
            if(!inputFile.canRead()){
                throw FileNotFoundException("Unable to read : $inputFile")
            }

            extractor = MediaExtractor()
            extractor!!.setDataSource(inputFile.toString())
            val trackIndex = selectTrack(extractor!!)
            if(trackIndex < 0){
                throw RuntimeException("No video track found in : $inputFile")
            }

            extractor!!.selectTrack(trackIndex)
            val format = extractor!!.getTrackFormat(trackIndex)

            val mime = format.getString(MediaFormat.KEY_MIME)
            decoder = MediaCodec.createDecoderByType(mime)
            decoder!!.configure(format, render.surface, null, 0)
            decoder!!.start()

            doExtract(extractor, trackIndex, decoder)
        }catch (e : Exception){
            e.printStackTrace()
        } finally {

            decoder?.apply {
                stop()
                release()
            }
            extractor?.apply {
                release()
            }
            render.release()
        }
    }

    private fun doExtract(extractor: MediaExtractor, trackIndex : Int, decoder : MediaCodec){

        val TIMEOUT_USEC = 10000L
        val decoderInputBuffers = decoder.getInputBuffers()
        val info = MediaCodec.BufferInfo()

        var inputChunk = 0
        var decodeCount = 0
        var frameSaveTime = 0L
        var outputDone = false
        var inputDone = false

        while (!outputDone){

            println("loop")

            if(!inputDone){
                var inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC)
                if(inputBufIndex >= 0){
                    var inputBuf = decoderInputBuffers[inputBufIndex]
                    var chunkSize = extractor.readSampleData(inputBuf, 0)
                    if(chunkSize < 0){
                        decoder.queueInputBuffer(inputBufIndex, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        inputDone = true
                        println("sent input EOS")
                    } else{
                        if(extractor.sampleTrackIndex != trackIndex){
                            println("WEIRD : got sample from track ${extractor.sampleTrackIndex} , expected $trackIndex")
                        }
                        var presentationTimeUs = extractor.sampleTime
                        decoder.queueInputBuffer(inputBufIndex, 0, chunkSize, presentationTimeUs, 0)
                        println("submitted frame $inputChunk to dec, size=$chunkSize")
                        inputChunk++
                        extractor.advance()
                    }
                } else {
                    println("input buffer not available.")
                }
            }

            if(!outputDone){
                var decoderStatus = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC)
                if(decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER){
                    //no output available yet
                    println("no output from decoder available")
                } else if(decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED){
                    println("decoder output buffers changes.")
                } else if(decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                    val newFormat = decoder.getOutputFormat()
                    println("decoder output format changed : $newFormat")
                } else if(decoderStatus < 0){
                    println("unexpected result from decoder.dequeueOutputBuffer : $decoderStatus")
                } else {
                    println("surface decoder given buffer : $decoderStatus (size = ${info.size} )")
                    if((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0){
                        println("output EOS")
                        outputDone = true
                    }

                    var doRender = (info.size != 0)
                    decoder.releaseOutputBuffer(decoderStatus, doRender)
                    if(doRender){
                        println("awaiting decode of frame $decodeCount")
                        render.awaitNewImage()
                        render.updateMatrix()
                        render.drawImage(false)

                        //todo 截图...
                        decodeCount++
                    }
                }
            }
        }

    }


    private fun selectTrack(extractor: MediaExtractor) : Int{

        val numTracks = extractor.trackCount
        for(i in 0..numTracks){
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if(mime.startsWith("video/")){
                println("Extractor selected track $i mime : $format")
                return i
            }
        }
        return -1
    }

    fun capture(view: View) {
        render.capture()
//        image_view.setImageBitmap(render.bitmap)
    }

}