package com.example.apparvoredavida.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.LoadParams
import androidx.paging.LoadResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Gerenciador de paginação para listas grandes.
 *
 * @param T Tipo de dados da lista
 * @param loadData Função que carrega os dados de uma página (page, pageSize) -> List<T>
 */
class PaginationManager<T : Any>(
    private val pageSize: Int = Constants.PAGE_SIZE,
    private val loadData: suspend (page: Int, pageSize: Int) -> List<T>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        // Retorna a página mais próxima do item âncora
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 0
            val items = withContext(Dispatchers.IO) {
                loadData(page, pageSize)
            }
            LoadResult.Page(
                data = items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (items.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
} 