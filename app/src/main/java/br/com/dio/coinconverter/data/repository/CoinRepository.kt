package br.com.dio.coinconverter.data.repository

import br.com.dio.coinconverter.data.model.ExchangeResponseValue
import kotlinx.coroutines.flow.Flow

interface CoinRepository {

    suspend fun getExchangeValue(coin: String): Flow<ExchangeResponseValue>

    suspend fun save(exchange: ExchangeResponseValue)

    fun list(): Flow<List<ExchangeResponseValue>>
}