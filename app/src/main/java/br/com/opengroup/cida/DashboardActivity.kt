package br.com.opengroup.cida

import LocalDatabaseHelper
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.opengroup.cida.api.InsightApi
import br.com.opengroup.cida.api.RetrofitHelper
import br.com.opengroup.cida.database.FirestoreLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardActivity : AppCompatActivity() {
    private lateinit var rvInsights: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var insightAdapter: InsightAdapter

    private val retrofit by lazy { RetrofitHelper.retrofit }
    private var currentPage = 1
    private var isLoading = false
    private var hasMorePages = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setupViews()
        setupRecyclerView()
        setupSwipeRefresh()
        loadInsights(refresh = true)
    }

    private fun setupViews() {
        rvInsights = findViewById(R.id.rvInsights)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        swipeRefresh = findViewById(R.id.swipeRefresh)
    }

    private fun setupRecyclerView() {
        insightAdapter = InsightAdapter()
        rvInsights.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = insightAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && hasMorePages) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                        ) {
                            loadInsights(refresh = false)
                        }
                    }
                }
            })
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            currentPage = 1
            hasMorePages = true
            loadInsights(refresh = true)
        }
    }

    private fun loadInsights(refresh: Boolean) {
        if (isLoading) return
        isLoading = true

        if (refresh) {
            progressBar.visibility = View.VISIBLE
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofit.create(InsightApi::class.java)
                    .getInsight(LocalDatabaseHelper(this@DashboardActivity).selectCredenciais()?.second?.first.toString())
                Log.d("DashboardActivity", "Insight: ${response.toString()}")
                Log.d("DashboardActivity", "Insight response: ${response.body()}")
                Log.d("DashboardActivity", "Response code: ${response.code()}")
                Log.d("DashboardActivity", "Response message: ${response.message()}")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val insightPage = response.body()
                        if (insightPage != null) {
                            if (refresh) {
                                insightAdapter.setItems(insightPage.insights.toList())
                            } else {
                                insightAdapter.addItems(insightPage.insights.toList())
                            }

                            hasMorePages = insightPage.total > (currentPage * insightPage.pageSize)
                            currentPage++

                            updateEmptyState(insightAdapter.itemCount == 0)
                        }
                    }
                    else {
                        FirestoreLogger.log("Erro ao obter insights, ${response.message()}", LocalDatabaseHelper(this@DashboardActivity).selectCredenciais()?.second?.first.toString())
                        withContext(Dispatchers.Main) {
                            // Handle error
                            updateEmptyState(insightAdapter.itemCount == 0)
                        }
                    }
                }
            } catch (e: Exception) {
                FirestoreLogger.log("Erro ao obter insights, ${e.message}", LocalDatabaseHelper(this@DashboardActivity).selectCredenciais()?.second?.first.toString())
                withContext(Dispatchers.Main) {
                    // Handle error
                    updateEmptyState(insightAdapter.itemCount == 0)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                    isLoading = false
                }
            }
        }
    }

    private fun updateEmptyState(show: Boolean) {
        tvEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        rvInsights.visibility = if (show) View.GONE else View.VISIBLE
    }
}

