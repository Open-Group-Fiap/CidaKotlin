package br.com.opengroup.cida

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.opengroup.cida.model.Insight
import io.noties.markwon.Markwon

class InsightAdapter : RecyclerView.Adapter<InsightAdapter.InsightViewHolder>() {
    private val insights = mutableListOf<Insight>()

    fun setItems(newItems: List<Insight>) {
        insights.clear()
        insights.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<Insight>) {
        val startPos = insights.size
        insights.addAll(newItems)
        notifyItemRangeInserted(startPos, newItems.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_insight, parent, false)
        return InsightViewHolder(view)
    }

    override fun onBindViewHolder(holder: InsightViewHolder, position: Int) {
        holder.bind(insights[position])
    }

    override fun getItemCount() = insights.size

    class InsightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvInsightId: TextView = itemView.findViewById(R.id.tvInsightId)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val markwon = Markwon.create(itemView.context)

        fun bind(insight: Insight) {
            tvInsightId.text = "Insight #${insight.idInsight}"
            tvDate.text = insight.dataGeracao
            // Render markdown text
            markwon.setMarkdown(tvDescription, insight.descricao)
        }
    }
}