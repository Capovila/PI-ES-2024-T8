package br.com.projetopi.smartlock

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object BitmapHelper {
    /**
     * Converte um vetor para um bitmap colorido.
     *
     * @param context O contexto atual.
     * @param id O ID do recurso do vetor.
     * @param color A cor para aplicar ao vetor.
     * @return Um BitmapDescriptor do vetor com a cor especificada.
     */
    fun vectorToBitmap (
        context: Context,
        @DrawableRes id: Int,
        @ColorInt color: Int
    ): BitmapDescriptor{
        // Obtém o vetor drawable
        val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, null)
            ?: return BitmapDescriptorFactory.defaultMarker()

        // Cria um bitmap com base nas dimensões do vetor drawable
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // Desenha o vetor no bitmap
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)

        // Retorna um BitmapDescriptor do bitmap
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}