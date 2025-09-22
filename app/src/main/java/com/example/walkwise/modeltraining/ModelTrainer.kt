package com.example.walkwise.modeltraining

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.walkwise.model.ModelVersionManager
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.channels.FileChannel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow
import kotlin.math.sqrt

class ModelTrainer(private val context: Context) {
    private val interpreter: Interpreter
    private lateinit var means: DoubleArray
    private lateinit var stds: DoubleArray

    init {
        val assetFileDescriptor = context.assets.openFd("mobility_model.tflite")
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

        interpreter = Interpreter(mappedByteBuffer)

        preprocessData()
    }

    companion object {
        private var instance: ModelTrainer? = null

        fun getInstance(context: Context): ModelTrainer {
            return instance ?: synchronized(this) {
                instance ?: ModelTrainer(context).also { instance = it }
            }
        }
    }

    private fun preprocessData() {
        val dataFile = context.assets.open("complete_training_set.csv")
        val records = BufferedReader(InputStreamReader(dataFile)).useLines { sequence ->
            sequence.map { it.split(",") }.toList()
        }

        val features = mutableListOf<DoubleArray>()

        for ((index, record) in records.withIndex()) {
            if (index == 0) continue // Skip header
            val featureArray = doubleArrayOf(
                record[0].toDouble(),
                record[1].toDouble(),
                record[2].toDouble(),
                record[3].toDouble(),
                record[5].toDouble(),
                record[7].toDouble()
            )
            features.add(featureArray)
        }

        means = DoubleArray(6) { 0.0 }
        stds = DoubleArray(6) { 1.0 }

        for (i in 0 until 6) {
            val columnValues = features.map { it[i] }
            means[i] = columnValues.average()
            stds[i] = sqrt(columnValues.map { (it - means[i]).pow(2) }.average())
        }
    }

    fun trainModel(): String {
        var lastLoss = 0f
        val file = File(context.filesDir, "model/travel_time_data.csv")
        if (file.exists()) {
            val records = BufferedReader(InputStreamReader(FileInputStream(file))).useLines { sequence ->
                sequence.map { it.split(",") }.toList()
            }

            val features = mutableListOf<DoubleArray>()
            val targets = mutableListOf<FloatArray>()

            for ((index, record) in records.withIndex()) {
                if (index == 0) continue
                val featureArray = doubleArrayOf(
                    record[0].toDouble(),
                    record[1].toDouble(),
                    record[2].toDouble(),
                    record[3].toDouble(),
                    record[5].toDouble(),
                    record[7].toDouble()
                )
                features.add(featureArray)
                targets.add(
                    floatArrayOf(
                        record[4].toFloat(),
                        record[6].toFloat()
                    )
                )
            }

            val X_train_scaled = features.map { feature ->
                DoubleArray(feature.size) { i -> (feature[i] - means[i]) / stds[i] }
            }.toTypedArray()

            val epochs = 2
            val batchSize = 1
            val inputs: MutableMap<String, Any> = HashMap()

            for (epoch in 0 until epochs) {
                Log.d("mytag", "epoch $epoch")
                var epochTotalLoss = 0f
                var epochWalkingLoss = 0f
                var epochTransportLoss = 0f
                var numBatches = 0

                for (i in X_train_scaled.indices step batchSize) {
                    val xBatch = mutableListOf<List<Float>>()
                    val yBatch = mutableListOf<List<Float>>()

                    for (j in i until minOf(i + batchSize, X_train_scaled.size)) {
                        val auxX = X_train_scaled[j].map { it.toFloat() }.toList()
                        xBatch.add(auxX)
                        val auxY = targets[j].map { it.toFloat() }.toList()
                        yBatch.add(auxY)
                    }

                    for (index in xBatch.indices) {
                        val f = ByteBuffer.allocateDirect(4 * xBatch[index].size)
                            .order(ByteOrder.nativeOrder())
                        xBatch[index].forEach { f.putFloat(it) }
                        f.rewind()

                        val l = ByteBuffer.allocateDirect(4 * yBatch[index].size)
                            .order(ByteOrder.nativeOrder())
                        yBatch[index].forEach { l.putFloat(it) }
                        l.rewind()

                        inputs["x"] = f
                        inputs["y"] = l

                        val outputs: MutableMap<String, Any> = HashMap()
                        val total_loss = FloatBuffer.allocate(1)
                        val walking_time_loss = FloatBuffer.allocate(1)
                        val transport_time_loss = FloatBuffer.allocate(1)

                        outputs["total_loss"] = total_loss
                        outputs["walking_time_loss"] = walking_time_loss
                        outputs["transport_time_loss"] = transport_time_loss

                        interpreter.runSignature(inputs, outputs, "train")

                        total_loss.rewind()
                        walking_time_loss.rewind()
                        transport_time_loss.rewind()

                        val batchTotalLoss = total_loss.get()
                        val batchWalkingLoss = walking_time_loss.get()
                        val batchTransportLoss = transport_time_loss.get()

                        epochTotalLoss += batchTotalLoss
                        epochWalkingLoss += batchWalkingLoss
                        epochTransportLoss += batchTransportLoss

                        numBatches++
                    }
                }

                val averageTotalLoss = epochTotalLoss / numBatches
                val averageWalkingLoss = epochWalkingLoss / numBatches
                val averageTransportLoss = epochTransportLoss / numBatches

                Log.d("mytag", "Epoch $epoch, Average Total Loss: $averageTotalLoss")
                Log.d("mytag", "Epoch $epoch, Average Walking Time Loss: $averageWalkingLoss")
                Log.d("mytag", "Epoch $epoch, Average Transport Time Loss: $averageTransportLoss")

                lastLoss = averageTotalLoss
            }
        } else {
            Log.d("mytag", "File does not exist!")
        }
        val formattedLoss = String.format("%.6f", lastLoss)
//        Log.d("mytag", "Training lastLoss: $formattedLoss")
        return formattedLoss
    }


    private fun manualStandardScale(data: Array<DoubleArray>, means: DoubleArray, stds: DoubleArray): Array<DoubleArray> {
        return data.map { feature ->
            DoubleArray(6) { i -> (feature[i] - means[i]) / stds[i] }
        }.toTypedArray()
    }

    data class Prediction(
        val walkingTime: Float,
        val busTravelTime: Float
    )

    fun infer(
        input1: Double,
        input2: Double,
        input3: Double,
        input4: Double,
        input5: Double,
        input6: Double
    ): Prediction {
        val inputs: MutableMap<String, Any> = HashMap()
        val outputs: MutableMap<String, Any> = HashMap()

        val x = arrayOf(
            doubleArrayOf(input1, input2, input3, input4, input5, input6)
        )
        val inputsScaled = manualStandardScale(x, means, stds)
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputsScaled[0].size).order(ByteOrder.nativeOrder())
        val floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer.put(inputsScaled[0].map { it.toFloat() }.toFloatArray())
        floatBuffer.rewind()

        inputs["x"] = floatBuffer

        val outputPredictions = FloatBuffer.allocate(2)

        outputs["predictions"] = outputPredictions

        interpreter.runSignature(inputs, outputs, "infer")

        outputPredictions.rewind()

        return Prediction(
            walkingTime = outputPredictions.get(),
            busTravelTime = outputPredictions.get()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun save(): String? {
        try {
            val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmssSSSS"))
            val modelDir = File(context.filesDir, "model")
            if (!modelDir.exists()) {
                modelDir.mkdirs()  // Create the directory if it doesn't exist
            }
            val outputFile = File(modelDir, "checkpoint-$time" + "_" + ModelVersionManager.getInstance(context).modelVersion + ".ckpt")
            val inputs: MutableMap<String, Any> = HashMap()
            inputs["checkpoint_path"] = outputFile.absolutePath
            val outputs: Map<String, Any> = HashMap()
            interpreter.runSignature(inputs, outputs, "save")
            val inputs2: MutableMap<String, Any> = HashMap()
            inputs2["checkpoint_path"] = outputFile.absolutePath
            val outputs2: Map<String, Any> = HashMap()
            interpreter.runSignature(inputs2, outputs2, "restore")
            return outputFile.absolutePath
        } catch (e: Exception) {
            Log.e("ModelCheckpointManager", "Error saving model checkpoint: ${e.message}")
        }
        return null
    }

    fun isCheckpointSaved(filePath: String): Boolean {
        val checkpointFile = File(filePath)
        return checkpointFile.exists()
    }

    fun restoreModel(checkpointPath: String): Boolean {
        val checkpointFile = File(context.filesDir, "model/averaged_model.ckpt")

        if (isCheckpointSaved(checkpointPath)) {
            Log.d("mytag", "fisierul e unde trebuie")
        } else {
            Log.d("mytag", "fisierul nu e unde trebuie")
        }

        val inputs: MutableMap<String, Any> = HashMap()
        inputs["checkpoint_path"] = checkpointFile.absolutePath

        val outputs: Map<String, Any> = HashMap()

        interpreter.runSignature(inputs, outputs, "restore")

        return outputs.isNotEmpty()
    }
}
