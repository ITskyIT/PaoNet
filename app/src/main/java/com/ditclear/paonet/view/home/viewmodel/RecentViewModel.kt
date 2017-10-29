package com.ditclear.paonet.view.home.viewmodel

import android.databinding.ObservableArrayList
import android.util.Log
import com.ditclear.paonet.lib.extention.async
import com.ditclear.paonet.model.remote.api.PaoService
import com.ditclear.paonet.view.article.viewmodel.ArticleItemViewModel
import com.ditclear.paonet.viewmodel.PagedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * 页面描述：RecentViewModel
 *
 * Created by ditclear on 2017/10/22.
 */
class RecentViewModel @Inject constructor(private val repo: PaoService) : PagedViewModel() {

    val sliders = ObservableArrayList<ArticleItemViewModel>()
    val obserableList = ObservableArrayList<ArticleItemViewModel>()

    fun loadData(isRefresh: Boolean) =
        repo.getSlider()
                .async()
                .doOnSuccess { t ->
                    Log.d("thread------",Thread.currentThread().name)
                    sliders.clear()
                    with(t) {
                        items?.map { ArticleItemViewModel(it) }?.let { sliders.addAll(it) }
                    }
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    Log.d("thread------",Thread.currentThread().name)
                    repo.getArticleList(page = 0) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { startLoad() }
                .doFinally{ stopLoad()}
                .doOnSuccess{ articleList ->
                    Log.d("thread------",Thread.currentThread().name)
                    with(articleList) {
                        if (isRefresh) {
                            obserableList.clear()
                        }
                        items?.map { ArticleItemViewModel(it) }?.let { obserableList.addAll(it) }
                    }
                }


}