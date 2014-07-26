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
package com.xeiam.xchange.bter.dto.marketdata;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.bter.BTERAdapters;
import com.xeiam.xchange.bter.dto.marketdata.BTERMarketInfoWrapper.BTERMarketInfoWrapperDeserializer;
import com.xeiam.xchange.currency.CurrencyPair;

@JsonDeserialize(using = BTERMarketInfoWrapperDeserializer.class)
public class BTERMarketInfoWrapper {

  private final Map<CurrencyPair, BTERMarketInfo> marketInfoMap;

  private BTERMarketInfoWrapper(final Map<CurrencyPair, BTERMarketInfo> marketInfoMap) {

    this.marketInfoMap = marketInfoMap;
  }

  public Map<CurrencyPair, BTERMarketInfo> getMarketInfoMap() {

    return marketInfoMap;
  }

  @Override
  public String toString() {

    return "BTERMarketInfoWrapper [marketInfoMap=" + marketInfoMap + "]";
  }

  public static class BTERMarketInfo {

    private final CurrencyPair currencyPair;
    private final int decimalPlaces;
    private final BigDecimal minAmount;
    private final BigDecimal fee;

    public BTERMarketInfo(CurrencyPair currencyPair, int decimalPlaces, BigDecimal minAmount, BigDecimal fee) {

      this.currencyPair = currencyPair;
      this.decimalPlaces = decimalPlaces;
      this.minAmount = minAmount;
      this.fee = fee;
    }

    public CurrencyPair getCurrencyPair() {

      return currencyPair;
    }

    public int getDecimalPlaces() {

      return decimalPlaces;
    }

    public BigDecimal getMinAmount() {

      return minAmount;
    }

    public BigDecimal getFee() {

      return fee;
    }

    @Override
    public String toString() {

      return "BTERMarketInfo [currencyPair=" + currencyPair + ", decimalPlaces=" + decimalPlaces + ", minAmount=" + minAmount + ", fee=" + fee + "]";
    }

  }

  static class BTERMarketInfoWrapperDeserializer extends JsonDeserializer<BTERMarketInfoWrapper> {

    @Override
    public BTERMarketInfoWrapper deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

      Map<CurrencyPair, BTERMarketInfo> marketInfoMap = new HashMap<CurrencyPair, BTERMarketInfo>();

      ObjectCodec oc = jp.getCodec();
      JsonNode marketsNodeWrapper = oc.readTree(jp);
      JsonNode marketNodeList = marketsNodeWrapper.path("pairs");

      if (marketNodeList.isArray()) {
        for (JsonNode marketNode : marketNodeList) {
          Iterator<Map.Entry<String, JsonNode>> iter = marketNode.fields();
          if (iter.hasNext()) {
            Entry<String, JsonNode> entry = iter.next();
            CurrencyPair currencyPair = BTERAdapters.adaptCurrencyPair(entry.getKey());
            JsonNode marketInfoData = entry.getValue();
            int decimalPlaces = marketInfoData.path("decimal_places").asInt();
            BigDecimal minAmount = new BigDecimal(marketInfoData.path("min_amount").asText());
            BigDecimal fee = new BigDecimal(marketInfoData.path("fee").asText());
            BTERMarketInfo marketInfoObject = new BTERMarketInfo(currencyPair, decimalPlaces, minAmount, fee);

            marketInfoMap.put(currencyPair, marketInfoObject);
          }
          else {
            throw new ExchangeException("Invalid market info response received from BTER." + marketsNodeWrapper);
          }
        }
      }
      else {
        throw new ExchangeException("Invalid market info response received from BTER." + marketsNodeWrapper);
      }

      return new BTERMarketInfoWrapper(marketInfoMap);
    }
  }
}
