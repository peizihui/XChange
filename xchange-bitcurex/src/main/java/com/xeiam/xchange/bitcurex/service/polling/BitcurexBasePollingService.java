/**
 * The MIT License
 * Copyright (c) 2012 Xeiam LLC http://xeiam.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.xeiam.xchange.bitcurex.service.polling;

import java.util.Arrays;
import java.util.List;

import com.xeiam.xchange.bitcurex.BitcurexExchange;
import si.mazi.rescu.RestProxyFactory;

import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.bitcurex.Bitcurex;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.service.BaseExchangeService;
import com.xeiam.xchange.service.polling.BasePollingService;

import static com.xeiam.xchange.currency.Currencies.EUR;
import static com.xeiam.xchange.currency.Currencies.PLN;

/**
 * @author timmolter
 */
public class BitcurexBasePollingService extends BaseExchangeService implements BasePollingService {
	
  protected Bitcurex bitcurex;

  public final List<CurrencyPair> CURRENCY_PAIRS;

  /**
   * Constructor
   * 
   * @param exchangeSpecification
   */
  public BitcurexBasePollingService(ExchangeSpecification exchangeSpecification) {

    super(exchangeSpecification);

    bitcurex = RestProxyFactory.createProxy(Bitcurex.class, exchangeSpecification.getSslUri());

    String currency = (String) exchangeSpecification.getExchangeSpecificParametersItem(BitcurexExchange.KEY_CURRENCY);
    CurrencyPair pair = currency.equals(EUR) ? CurrencyPair.BTC_EUR : CurrencyPair.BTC_PLN;
    CURRENCY_PAIRS = Arrays.asList(pair);
  }

  public void verify(String currency) throws ExchangeException {
	  if(!currency.equalsIgnoreCase(EUR) && !currency.equalsIgnoreCase(PLN))
		  throw new ExchangeException("Invalid currency: " + currency);
  }

  @Override
  public List<CurrencyPair> getExchangeSymbols() {

    return CURRENCY_PAIRS;
  }
  
}
