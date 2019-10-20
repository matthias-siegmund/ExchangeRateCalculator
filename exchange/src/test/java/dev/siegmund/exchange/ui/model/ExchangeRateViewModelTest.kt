package dev.siegmund.exchange.ui.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import dev.siegmund.exchange.TestSchedulers
import dev.siegmund.exchange.api.ExchangeRateRepository
import dev.siegmund.exchange.api.model.ExchangeRateResponse
import dev.siegmund.exchange.ui.util.CurrencyNameProvider
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.test.assertTrue

class ExchangeRateViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var exchangeRateItemsObserver: Observer<List<ExchangeRateItem>>

    @Mock
    lateinit var showErrorObserver: Observer<Unit>

    @Mock
    lateinit var scrollToTopObserver: Observer<String>

    @Mock
    lateinit var exchangeRateRepository: ExchangeRateRepository

    @Mock
    lateinit var currencyNameProvider: CurrencyNameProvider

    private lateinit var viewModel: ExchangeRateViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        whenever(currencyNameProvider.getDisplayName(EUR_CODE)).thenReturn(EUR_NAME)
        whenever(currencyNameProvider.getDisplayName(AUD_CODE)).thenReturn(AUD_NAME)
        whenever(currencyNameProvider.getDisplayName(BGN_CODE)).thenReturn(BGN_NAME)
        whenever(exchangeRateRepository.observeExchangeRates(BASE))
            .thenReturn(Observable.just(response))

        viewModel = ExchangeRateViewModel(
            exchangeRateRepository,
            TestSchedulers.schedulers,
            currencyNameProvider
        )
    }

    @Test
    fun `exchange rates are observed with default base when view model is created`() {
        // given
        whenever(exchangeRateRepository.observeExchangeRates(BASE))
            .thenReturn(Observable.just(response))

        // when
        viewModel.onCreate()

        // then
        verify(exchangeRateRepository, times(1)).observeExchangeRates(BASE)
    }

    @Test
    fun `exchange rates are updated when view model is created`() {
        // given
        whenever(exchangeRateRepository.observeExchangeRates(BASE))
            .thenReturn(Observable.just(response))
        viewModel.exchangeRateItems.observeForever(exchangeRateItemsObserver)

        // when
        viewModel.onCreate()

        // then
        val captor = argumentCaptor<List<ExchangeRateItem>>()
        verify(exchangeRateItemsObserver, times(1)).onChanged(captor.capture())
        assertTrue {
            captor.firstValue[0].currencyCode == EUR_CODE &&
                    captor.firstValue[0].currencyName == EUR_NAME &&
                    captor.firstValue[0].rate == EUR_RATE &&
                    captor.firstValue[0].value == 1.0 &&
                    !captor.firstValue[0].clickable &&
                    captor.firstValue[0].editable &&
                    captor.firstValue[1].currencyCode == AUD_CODE &&
                    captor.firstValue[1].currencyName == AUD_NAME &&
                    captor.firstValue[1].rate == AUD_RATE &&
                    captor.firstValue[1].value == AUD_RATE &&
                    captor.firstValue[1].clickable &&
                    !captor.firstValue[1].editable &&
                    captor.firstValue[2].currencyCode == BGN_CODE &&
                    captor.firstValue[2].currencyName == BGN_NAME &&
                    captor.firstValue[2].rate == BGN_RATE &&
                    captor.firstValue[2].value == BGN_RATE &&
                    captor.firstValue[2].clickable &&
                    !captor.firstValue[2].editable
        }
    }

    @Test
    fun `exchange rates are not updated when error occurs`() {
        // given
        whenever(exchangeRateRepository.observeExchangeRates(BASE))
            .thenReturn(Observable.error(IllegalStateException()))
        viewModel.exchangeRateItems.observeForever(exchangeRateItemsObserver)

        // when
        viewModel.onCreate()

        // then
        verify(exchangeRateItemsObserver, never()).onChanged(any())
    }

    @Test
    fun `error is shown when error occurs`() {
        // given
        whenever(exchangeRateRepository.observeExchangeRates(BASE))
            .thenReturn(Observable.error(IllegalStateException()))
        viewModel.showError.observeForever(showErrorObserver)

        // when
        viewModel.onCreate()

        // then
        verify(showErrorObserver, times(1)).onChanged(null)
    }

    @Test
    fun `exchange rates are observed with new base when item was clicked`() {
        // given
        whenever(exchangeRateRepository.observeExchangeRates(AUD_CODE))
            .thenReturn(Observable.just(response))

        // when
        viewModel.onItemClick(aud)

        // then
        verify(exchangeRateRepository, times(1)).observeExchangeRates(AUD_CODE)
    }

    @Test
    fun `list scrolled to top when item was clicked`() {
        // given
        whenever(exchangeRateRepository.observeExchangeRates(AUD_CODE))
            .thenReturn(Observable.just(response))
        viewModel.scrollToTop.observeForever(scrollToTopObserver)

        // when
        viewModel.onItemClick(aud)

        // then
        verify(scrollToTopObserver, times(1)).onChanged(AUD_CODE)
    }

    @Test
    fun `exchange rates are observed with same base when item value changed`() {
        // given
        val newValue = 5.0
        whenever(exchangeRateRepository.observeExchangeRates(BASE))
            .thenReturn(Observable.just(response))

        // when
        viewModel.onValueChanged(eur.copy(value = newValue), listOf(eur, aud, bgn))

        // then
        verify(exchangeRateRepository, times(1)).observeExchangeRates(BASE)
    }

    @Test
    fun `exchange rates are updated when item value changed`() {
        // given
        val newValue = 5.0
        whenever(exchangeRateRepository.observeExchangeRates(BASE))
            .thenReturn(Observable.just(response))
        viewModel.exchangeRateItems.observeForever(exchangeRateItemsObserver)

        // when
        viewModel.onValueChanged(eur.copy(value = newValue), listOf(eur, aud, bgn))

        // then
        val captor = argumentCaptor<List<ExchangeRateItem>>()
        verify(exchangeRateItemsObserver, times(2)).onChanged(captor.capture())
        assertTrue {
            captor.secondValue[0].currencyCode == EUR_CODE &&
                    captor.secondValue[0].currencyName == EUR_NAME &&
                    captor.secondValue[0].rate == EUR_RATE &&
                    captor.secondValue[0].value == newValue &&
                    !captor.secondValue[0].clickable &&
                    captor.secondValue[0].editable &&
                    captor.secondValue[1].currencyCode == AUD_CODE &&
                    captor.secondValue[1].currencyName == AUD_NAME &&
                    captor.secondValue[1].rate == AUD_RATE &&
                    captor.secondValue[1].value == newValue * AUD_RATE &&
                    captor.secondValue[1].clickable &&
                    !captor.secondValue[1].editable &&
                    captor.secondValue[2].currencyCode == BGN_CODE &&
                    captor.secondValue[2].currencyName == BGN_NAME &&
                    captor.secondValue[2].rate == BGN_RATE &&
                    captor.secondValue[2].value == newValue * BGN_RATE &&
                    captor.secondValue[2].clickable &&
                    !captor.secondValue[2].editable
        }
    }

    private companion object {
        const val EUR_CODE = "EUR"
        const val EUR_NAME = "Euro"
        const val EUR_RATE = 1.0
        val eur = ExchangeRateItem(
            currencyName = EUR_NAME,
            currencyCode = EUR_CODE,
            value = 1.0,
            rate = EUR_RATE,
            editable = false,
            clickable = true
        )

        const val AUD_CODE = "AUD"
        const val AUD_NAME = "Australian dollar"
        const val AUD_RATE = 1.61
        val aud = ExchangeRateItem(
            currencyName = AUD_NAME,
            currencyCode = AUD_CODE,
            value = 1.0,
            rate = AUD_RATE
        )

        const val BGN_CODE = "BGN"
        const val BGN_NAME = "Australian dollar"
        const val BGN_RATE = 1.94
        val bgn = ExchangeRateItem(
            currencyName = BGN_NAME,
            currencyCode = BGN_CODE,
            value = 1.0,
            rate = BGN_RATE
        )

        const val BASE = "EUR"
        val response = ExchangeRateResponse(
            base = BASE,
            date = Date(),
            rates = mapOf(
                AUD_CODE to AUD_RATE,
                BGN_CODE to BGN_RATE
            )
        )
    }
}