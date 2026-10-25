package dev.lumentv

import android.graphics.Color
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter

data class Command(val label: String, val action: () -> Unit)
class Cards : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val card = ImageCardView(parent.context).apply {
            isFocusable = true; isFocusableInTouchMode = true
            setMainImageDimensions(Ui.dp(context,118), Ui.dp(context,164))
            mainImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundColor(Ui.PANEL); setInfoAreaBackgroundColor(Ui.PANEL)
            setOnFocusChangeListener { _, focused ->
                setInfoAreaBackgroundColor(if(focused) Ui.ACCENT else Ui.PANEL)
                findViewById<android.widget.TextView>(androidx.leanback.R.id.title_text)?.setTextColor(if(focused) Ui.BG else Color.WHITE)
            }
        }
        return ViewHolder(card)
    }
    override fun onBindViewHolder(holder: ViewHolder, item: Any) {
        val card = holder.view as ImageCardView
        when(item) {
            is Title -> { card.titleText = item.name; card.contentText = "${item.year}  ${item.type.uppercase()}"; Posters.load(card.mainImageView,item.poster) }
            is Playback -> { card.titleText = item.label; card.contentText = "Resume · ${(item.seconds / 60).toInt()} min"; Posters.load(card.mainImageView,item.title.poster) }
            is Command -> { card.titleText = item.label; card.contentText = "Select to open"; card.mainImageView.tag = null; card.mainImageView.setImageResource(R.drawable.icon) }
        }
    }
    override fun onUnbindViewHolder(holder: ViewHolder) { (holder.view as ImageCardView).mainImageView.apply { tag = null; setImageDrawable(null) } }
}
