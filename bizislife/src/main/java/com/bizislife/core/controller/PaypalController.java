package com.bizislife.core.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentReq;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentRequestType;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsReq;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsReq;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsRequestType;
import urn.ebay.api.PayPalAPI.GetTransactionDetailsResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutReq;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutRequestType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.AddressType;
import urn.ebay.apis.eBLBaseComponents.BillingAgreementDetailsType;
import urn.ebay.apis.eBLBaseComponents.BillingCodeType;
import urn.ebay.apis.eBLBaseComponents.CountryCodeType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.DoExpressCheckoutPaymentRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.ItemCategoryType;
import urn.ebay.apis.eBLBaseComponents.PaymentActionCodeType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsItemType;
import urn.ebay.apis.eBLBaseComponents.PaymentDetailsType;
import urn.ebay.apis.eBLBaseComponents.PaymentInfoType;
import urn.ebay.apis.eBLBaseComponents.SetExpressCheckoutRequestDetailsType;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.paypal.*;
import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Controller
public class PaypalController {
	
	private static int DECIMALS = 2;
	private static int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;
	
	private Properties paypalConfig;
	
	@Autowired 
	private ApplicationContext appContext;
	
	@Autowired
	private ApplicationConfiguration appConfig;
	
	@PostConstruct
	protected void init() {
		try {
			Resource res = appContext.getResource("classpath:paypal_configuration.properties");
			if (res==null) throw new IllegalArgumentException("Cannot find configuration file: paypal_config.properties");
			InputStream in = res.getInputStream();
			Reader r = new InputStreamReader(in,"UTF-8");
			paypalConfig = new Properties();
			paypalConfig.load(r);
			r.close();
		} catch (java.io.IOException e) {
			throw new IllegalArgumentException("Cannot read paypal_configuration.properties", e);
		}
	} // init
	
	
	
	@RequestMapping(value="/paypalExpressTest", method=RequestMethod.GET)
	public String paypaltesting(HttpServletResponse res, HttpServletRequest req) throws ClientProtocolException, IOException{
		
		CartItemBean item1 = new CartItemBean();
		item1.setAmount("1");
		item1.setCategory(ItemCategoryType.PHYSICAL.getValue());
		item1.setDescription("item1 description");
		item1.setName("item #1");
		item1.setQuantity(1);
		item1.setSalesTax("0.15");
		CartItemBean item2 = new CartItemBean();
		item2.setAmount("2");
		item2.setCategory(ItemCategoryType.PHYSICAL.getValue());
		item2.setDescription("item2 description");
		item2.setName("item #2");
		item2.setQuantity(2);
		item2.setSalesTax("0.3");
		
		PaymentDetailBean paymentDetail = new PaymentDetailBean();
		paymentDetail.setBillingAgreementText("");
		paymentDetail.setBillingType(BillingCodeType.MERCHANTINITIATEDBILLING.getValue());
		paymentDetail.setCancelUrl("http://www.cancel.com");
		paymentDetail.setCurrencyCode(CurrencyCodeType.CAD.getValue());
		paymentDetail.setErrorUrl("http://www.errorurl.coom");
		paymentDetail.setHandlingTotal("0.01");
		paymentDetail.setInsuranceTotal("0.02");
		paymentDetail.setNotifyURL("http://www.notifyurl.com");
		paymentDetail.setOrderDescription("test order");
		paymentDetail.setOrgUuid("a6233f01-31ee-4c96-9c23-1c828b647e1d");
		paymentDetail.setPaymentType(PaymentActionCodeType.SALE.getValue());
		paymentDetail.setReturnUrl("http://www.returnurl.com");
		paymentDetail.setSellerAccountName("stone.gu-facilitator_api1.gmail.com");
		paymentDetail.setSellerAccountPwd("1363623204");
		paymentDetail.setSellerAppid("APP-80W284485P519543T");
		paymentDetail.setSellerSignature("AKFsaOs1WEAn7y0OhOyPd.wPrECEAmPF0vFRE.JZWUF5pA9H3MwK9b1m");
		paymentDetail.setShippingTotal("0.03");
		paymentDetail.setTaxTotal("0.04");
		
		ShippingAddressBean shippingAddr = new ShippingAddressBean();
		shippingAddr.setAddressOverride(ShippingAddressBean.AddressOverride.paypalShouldNotDisplayShippingAddress.getCode());
		shippingAddr.setBuyerEmail("buyer@biz.com");
		shippingAddr.setCity("Toronto");
		shippingAddr.setCountryCode(CountryCodeType.US.getValue());
		shippingAddr.setNoShipping(ShippingAddressBean.NoShipping.DonotDisplayShippingAddressInPayPal.getCode());
		shippingAddr.setPostalCode("l3p3t3");
		shippingAddr.setReceiverName("Stone Gu");
		shippingAddr.setRequestConfirmShipping(ShippingAddressBean.ReqConfirmedShipping.no.getCode());
		shippingAddr.setState("Ontario");
		shippingAddr.setStreetAddress1("34 thatchers mill way");
		shippingAddr.setStreetAddress2("");
		
		PaypalExpressCheckoutDetails checkoutDetail = new PaypalExpressCheckoutDetails();
		checkoutDetail.setPaydetail(paymentDetail);
		checkoutDetail.setShippingAddr(shippingAddr);
		checkoutDetail.addCartItem(item1);
		checkoutDetail.addCartItem(item2);
		
		
		
		
		return "Done";
		
	}
	
	@RequestMapping(value="/paypalExpressCheckout", method=RequestMethod.POST)
	public @ResponseBody ApiResponse paypalExpressCheckout(HttpEntity<String> xmlString,
			HttpServletResponse response, HttpServletRequest request
	) throws IOException, SSLConfigurationException, InvalidCredentialException, HttpErrorException, InvalidResponseDataException, ClientActionRequiredException, MissingCredentialException, OAuthException, InterruptedException, ParserConfigurationException, SAXException{
		
		ApiResponse apiRes = null;
		
		if(xmlString!=null){
			String xml = xmlString.getBody();
			
			final XStream stream = new XStream(new DomDriver());
			stream.registerConverter(new PaypalExpressCheckoutDetails.PaypalExpressCheckoutDetailsConverter());
			stream.registerConverter(new CartItemBean.CartItemBeanConverter());
			stream.registerConverter(new PaymentDetailBean.PaymentDetailBeanConverter());
			stream.registerConverter(new ShippingAddressBean.ShippingAddressBeanConverter());
			stream.processAnnotations(new Class[]{PaypalExpressCheckoutDetails.class, CartItemBean.class, PaymentDetailBean.class, ShippingAddressBean.class});

			PaypalExpressCheckoutDetails ppReqDetail = (PaypalExpressCheckoutDetails)stream.fromXML(xml);
			
			if(ppReqDetail!=null){
				
				
				List<CartItemBean> itemsBean = ppReqDetail.getItems();
				PaymentDetailBean payDetailBean = ppReqDetail.getPaydetail();
				ShippingAddressBean shippingAddrBean = ppReqDetail.getShippingAddr();

				//PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(this.getClass().getResourceAsStream("/paypal_config.properties"));
				// replace the default seller account info with the info in xml
				if(paypalConfig!=null){
					if(StringUtils.isNotBlank(payDetailBean.getSellerAccountName())){
						paypalConfig.setProperty("acct1.UserName", payDetailBean.getSellerAccountName());
					}
					if(StringUtils.isNotBlank(payDetailBean.getSellerAccountPwd())){
						paypalConfig.setProperty("acct1.Password", payDetailBean.getSellerAccountPwd());
					}
					if(StringUtils.isNotBlank(payDetailBean.getSellerAppid())){
						paypalConfig.setProperty("acct1.AppId", payDetailBean.getSellerAppid());
					}
					if(StringUtils.isNotBlank(payDetailBean.getSellerSignature())){
						paypalConfig.setProperty("acct1.Signature", payDetailBean.getSellerSignature());
					}
				}else{
					apiRes = new ApiResponse();
					apiRes.setSuccess(false);
					apiRes.setResponse1("No paypal configuration is found!");
					return apiRes;
				}
				PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(paypalConfig);
				
				if(service!=null && itemsBean!=null && itemsBean.size()>0 && payDetailBean!=null && shippingAddrBean!=null){
					// ....
					SetExpressCheckoutRequestType setExpressCheckoutReq = new SetExpressCheckoutRequestType();
					SetExpressCheckoutRequestDetailsType details = new SetExpressCheckoutRequestDetailsType();
					
					// All url info setup
					
					StringBuffer url = new StringBuffer(); // local url : http://localhost:8080
					url.append("http://");
					url.append(appConfig.getHostName());
//					url.append(request.getServerName());
//					url.append(":");
//					url.append(request.getServerPort());
//					url.append(request.getContextPath());

//					if(StringUtils.isNotBlank(payDetailBean.getReturnUrl())){
//						details.setReturnURL(payDetailBean.getReturnUrl());
//					}else{
//					}
					String returnURL = url.toString() + "/paypalExpressCheckout_continue";
					details.setReturnURL(returnURL + "?currencyCodeType=" + payDetailBean.getCurrencyCode()+"&returnurl="+URLEncoder.encode(payDetailBean.getReturnUrl(), "UTF-8")+"&errorurl="+URLEncoder.encode(payDetailBean.getErrorUrl(), "UTF-8"));



					details.setCancelURL(payDetailBean.getCancelUrl());
					details.setBuyerEmail(shippingAddrBean.getBuyerEmail());
					
					// set line items
					List<PaymentDetailsItemType> lineItems = new ArrayList<PaymentDetailsItemType>();
					
					BigDecimal itemTotal = new BigDecimal("0.00");
					BigDecimal orderTotal = new BigDecimal("0.00");
					for(CartItemBean itemBean : itemsBean){
						PaymentDetailsItemType item = new PaymentDetailsItemType();

						String itemAmount = itemBean.getAmount();
						Integer itemQuantity = itemBean.getQuantity();
						
						BasicAmountType amt = new BasicAmountType();
						amt.setCurrencyID(CurrencyCodeType.fromValue(payDetailBean.getCurrencyCode()));
						amt.setValue(itemAmount);

						item.setQuantity(itemQuantity);
						item.setName(itemBean.getName());
						item.setAmount(amt);
						item.setItemCategory(ItemCategoryType.fromValue(itemBean.getCategory()));
						item.setDescription(itemBean.getDescription());
						if (StringUtils.isNotBlank(itemBean.getSalesTax())) {
							item.setTax(new BasicAmountType(CurrencyCodeType.fromValue(payDetailBean.getCurrencyCode()), itemBean.getSalesTax()));					
						}
						lineItems.add(item);
						
						BigDecimal itemPrice = new BigDecimal(itemAmount);
						BigDecimal itemNumber = new BigDecimal(itemQuantity);
						itemTotal = itemTotal.add(itemPrice.multiply(itemNumber));
					}
					orderTotal = orderTotal.add(itemTotal);

					// pay detail
					List<PaymentDetailsType> payDetails = new ArrayList<PaymentDetailsType>();
					PaymentDetailsType paydtl = new PaymentDetailsType();
					paydtl.setPaymentAction(PaymentActionCodeType.fromValue(payDetailBean.getPaymentType()));
					if (StringUtils.isNotBlank(payDetailBean.getShippingTotal())) {
						BasicAmountType shippingTotal = new BasicAmountType();
						shippingTotal.setValue(payDetailBean.getShippingTotal());
						shippingTotal.setCurrencyID(CurrencyCodeType.fromValue(payDetailBean.getCurrencyCode()));
						BigDecimal shiptotal = new BigDecimal(payDetailBean.getShippingTotal());
						orderTotal = orderTotal.add(shiptotal);
						paydtl.setShippingTotal(shippingTotal);
					}
					if (StringUtils.isNotBlank(payDetailBean.getInsuranceTotal())) {
						paydtl.setInsuranceTotal(new BasicAmountType(CurrencyCodeType.fromValue(payDetailBean.getCurrencyCode()), payDetailBean.getInsuranceTotal()));
						paydtl.setInsuranceOptionOffered("true");
						BigDecimal insurTotal = new BigDecimal(payDetailBean.getInsuranceTotal());
						orderTotal = orderTotal.add(insurTotal);
					}
					if (StringUtils.isNotBlank(payDetailBean.getHandlingTotal())) {
						paydtl.setHandlingTotal(new BasicAmountType(CurrencyCodeType.fromValue(payDetailBean.getCurrencyCode()), payDetailBean.getHandlingTotal()));
						BigDecimal handlingtotal = new BigDecimal(payDetailBean.getHandlingTotal());
						orderTotal = orderTotal.add(handlingtotal);
					}
					if (StringUtils.isNotBlank(payDetailBean.getTaxTotal())) {
						paydtl.setTaxTotal(new BasicAmountType(CurrencyCodeType.fromValue(payDetailBean.getCurrencyCode()),payDetailBean.getTaxTotal()));
						BigDecimal taxtotal = new BigDecimal(payDetailBean.getTaxTotal());
						orderTotal = orderTotal.add(taxtotal);
					}
					if (StringUtils.isNotBlank(payDetailBean.getOrderDescription())) {
						paydtl.setOrderDescription(payDetailBean.getOrderDescription());
					}

					BasicAmountType itemsTotal = new BasicAmountType();
					itemsTotal.setValue(itemTotal.setScale(DECIMALS, ROUNDING_MODE).toPlainString());
					itemsTotal.setCurrencyID(CurrencyCodeType.fromValue(payDetailBean.getCurrencyCode()));
					paydtl.setOrderTotal(new BasicAmountType(CurrencyCodeType.fromValue(payDetailBean.getCurrencyCode()), orderTotal.setScale(DECIMALS, ROUNDING_MODE).toPlainString()));
					paydtl.setPaymentDetailsItem(lineItems);
					paydtl.setItemTotal(itemsTotal);
					paydtl.setNotifyURL(payDetailBean.getNotifyURL());
					payDetails.add(paydtl);
					
					details.setPaymentDetails(payDetails);
					
					if (StringUtils.isNotBlank(payDetailBean.getBillingAgreementText())) {
						BillingAgreementDetailsType billingAgreement = new BillingAgreementDetailsType(BillingCodeType.fromValue(payDetailBean.getBillingType()));
						billingAgreement.setBillingAgreementDescription(payDetailBean.getBillingAgreementText());
						List<BillingAgreementDetailsType> billList = new ArrayList<BillingAgreementDetailsType>();
						billList.add(billingAgreement);
						details.setBillingAgreementDetails(billList);
					}
					
					//shipping address
					if(StringUtils.isNotBlank(shippingAddrBean.getRequestConfirmShipping())){
						details.setReqConfirmShipping(shippingAddrBean.getRequestConfirmShipping());
					}
					if(StringUtils.isNotBlank(shippingAddrBean.getAddressOverride())){
						details.setAddressOverride(shippingAddrBean.getAddressOverride());
					}
					if(StringUtils.isNotBlank(shippingAddrBean.getCity()) && StringUtils.isNotBlank(shippingAddrBean.getState())){
						AddressType shipToAddress=new AddressType();
						shipToAddress.setName(shippingAddrBean.getReceiverName());
						shipToAddress.setStreet1(shippingAddrBean.getStreetAddress1());
						shipToAddress.setStreet2(shippingAddrBean.getStreetAddress2());
						shipToAddress.setCityName(shippingAddrBean.getCity());
						shipToAddress.setStateOrProvince(shippingAddrBean.getState());
						shipToAddress.setPostalCode(shippingAddrBean.getPostalCode());
						shipToAddress.setCountry(CountryCodeType.fromValue(shippingAddrBean.getCountryCode()));
						details.setAddress(shipToAddress);
					}
					
					// shipping display options
					if(StringUtils.isNotBlank(shippingAddrBean.getNoShipping())){
						details.setNoShipping(shippingAddrBean.getNoShipping());
					}
					
					// PayPal page styling attributes
					// ... omitted for first version.
					
					setExpressCheckoutReq.setSetExpressCheckoutRequestDetails(details);

					SetExpressCheckoutReq expressCheckoutReq = new SetExpressCheckoutReq();
					expressCheckoutReq.setSetExpressCheckoutRequest(setExpressCheckoutReq);

					SetExpressCheckoutResponseType setExpressCheckoutResponse = service.setExpressCheckout(expressCheckoutReq);

					if (setExpressCheckoutResponse != null) {
						if (setExpressCheckoutResponse.getAck().toString().equalsIgnoreCase("SUCCESS")) {
							apiRes = new ApiResponse();
							apiRes.setSuccess(true);
//							apiRes.setRet("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+setExpressCheckoutResponse.getToken());
							apiRes.setResponse1(setExpressCheckoutResponse.getToken());
							
							
						} else {

							apiRes = new ApiResponse();
							apiRes.setSuccess(false);
							apiRes.setResponse1(setExpressCheckoutResponse.getErrors());
							
						}
					}
					
				}
			}
		}
		
		return apiRes;
	}
	
	@RequestMapping(value="/paypalExpressCheckout_continue", method=RequestMethod.GET)
	public String paypalExpressCheckout_continue(
			@RequestParam(value = "token", required = true) String token, 
			@RequestParam(value = "returnurl", required = true) String rurl, 
			@RequestParam(value = "errorurl", required = true) String eurl, 
			HttpServletResponse response, HttpServletRequest request
	) throws IOException, SSLConfigurationException, InvalidCredentialException, HttpErrorException, InvalidResponseDataException, ClientActionRequiredException, MissingCredentialException, OAuthException, InterruptedException, ParserConfigurationException, SAXException{
		
		
		PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(paypalConfig);
		
		String returnurl = URLDecoder.decode(rurl, "UTF-8");
		String errorurl = URLDecoder.decode(eurl, "UTF-8");
		
		
		if(service!=null){
			GetExpressCheckoutDetailsReq req = new GetExpressCheckoutDetailsReq();
			GetExpressCheckoutDetailsRequestType reqType = new GetExpressCheckoutDetailsRequestType(token);

			req.setGetExpressCheckoutDetailsRequest(reqType);
			GetExpressCheckoutDetailsResponseType resp = service.getExpressCheckoutDetails(req);			
			
			if (resp != null) {
					String newToken = resp.getGetExpressCheckoutDetailsResponseDetails().getToken();
					String payerId = resp.getGetExpressCheckoutDetailsResponseDetails().getPayerInfo().getPayerID();
					
					if(StringUtils.isNotBlank(newToken) && StringUtils.isNotBlank(payerId)){
						// DoExpressCheckout
						DoExpressCheckoutPaymentRequestType doCheckoutPaymentRequestType = new DoExpressCheckoutPaymentRequestType();
						DoExpressCheckoutPaymentRequestDetailsType details = new DoExpressCheckoutPaymentRequestDetailsType();
						details.setToken(newToken);
						details.setPayerID(payerId);

						details.setPaymentDetails(resp.getGetExpressCheckoutDetailsResponseDetails().getPaymentDetails());
						
						doCheckoutPaymentRequestType.setDoExpressCheckoutPaymentRequestDetails(details);
						DoExpressCheckoutPaymentReq doExpressCheckoutPaymentReq = new DoExpressCheckoutPaymentReq();
						doExpressCheckoutPaymentReq.setDoExpressCheckoutPaymentRequest(doCheckoutPaymentRequestType);

						DoExpressCheckoutPaymentResponseType doCheckoutPaymentResponseType = service.doExpressCheckoutPayment(doExpressCheckoutPaymentReq);

						if (doCheckoutPaymentResponseType != null) {
							if (doCheckoutPaymentResponseType.getAck().toString().equalsIgnoreCase("SUCCESS")) {
								StringBuilder resultToReturn = new StringBuilder();
								
								Iterator<PaymentInfoType> iterator = doCheckoutPaymentResponseType.getDoExpressCheckoutPaymentResponseDetails().getPaymentInfo().iterator();
								int index = 1;
								while (iterator.hasNext()) {
									PaymentInfoType result = (PaymentInfoType) iterator.next();
									if(index>1){
										resultToReturn.append("&");
									}
									resultToReturn.append("TransactionID").append("=").append(result.getTransactionID());
									index++;
								}
								
								if(returnurl.indexOf("?")>-1){
									return "redirect:"+returnurl+"&"+resultToReturn;
								}else{
									return "redirect:"+returnurl+"?"+resultToReturn;
								}
							} else {
								return "redirect:"+errorurl;

							}
						}
						
					}
			}else {
					
				return "redirect:"+errorurl;
			}
			
		}
		
		return "redirect:"+errorurl;
	}
	
	
	
	@RequestMapping(value = "/getTransactionDetails", method = RequestMethod.POST)
	public @ResponseBody ApiResponse getTransactionDetails(
			HttpEntity<String> jsonString,
			HttpServletResponse httpRes, HttpServletRequest httpReq
			
	) throws SSLConfigurationException, InvalidCredentialException, HttpErrorException, InvalidResponseDataException, ClientActionRequiredException, MissingCredentialException, OAuthException, IOException, InterruptedException, ParserConfigurationException, SAXException {
		
		
		ApiResponse apires = null;

		if(jsonString!=null){
			String json = jsonString.getBody();
			JSONParser parser = new JSONParser();
			
			try{
				JSONObject obj = (JSONObject)parser.parse(json);
//				{"TransactionID":"9S791910CL4639010","paypal":{"appid":"APP-80W284485P519543T","username":"andrew+jtheline_api1.hubba.com","password":"1364320070","signature":"A7qFyS7y9Bdp7M07vvTlvA.ktvGxAEblyhQoffvfmLO6hVCdEY.6hWYK"}}				

				String transactionId = (String)obj.get("TransactionID");
				JSONObject paypalCred = (JSONObject)obj.get("paypal");
				String paypalAccountName = (String)paypalCred.get("username");
				String paypalAccountPwd = (String)paypalCred.get("password");
				String paypalSignature = (String)paypalCred.get("signature");
				String paypalAppid = (String)paypalCred.get("appid");
				
				if(paypalConfig!=null && StringUtils.isNotBlank(transactionId) && StringUtils.isNotBlank(paypalAccountName) && StringUtils.isNotBlank(paypalAccountPwd) && StringUtils.isNotBlank(paypalAppid) && StringUtils.isNotBlank(paypalSignature)){
					paypalConfig.setProperty("acct1.UserName", paypalAccountName);
					paypalConfig.setProperty("acct1.Password", paypalAccountPwd);
					paypalConfig.setProperty("acct1.AppId", paypalAppid);
					paypalConfig.setProperty("acct1.Signature", paypalSignature);
				
					
					PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(paypalConfig);

					
					GetTransactionDetailsReq req = new GetTransactionDetailsReq();
					GetTransactionDetailsRequestType reqType = new GetTransactionDetailsRequestType();
					reqType.setTransactionID(transactionId);
					req.setGetTransactionDetailsRequest(reqType);
					GetTransactionDetailsResponseType resp = service.getTransactionDetails(req);
					if (resp != null) {
						if (resp.getAck().toString().equalsIgnoreCase("SUCCESS")) {
							Map<Object, Object> map = new LinkedHashMap<Object, Object>();
							map.put("Ack", resp.getAck());
							map.put("Payer", resp.getPaymentTransactionDetails().getPayerInfo().getPayer());
							map.put("Gross Amount", resp.getPaymentTransactionDetails().getPaymentInfo().getGrossAmount().getValue()
									+ " "
									+ resp.getPaymentTransactionDetails()
											.getPaymentInfo().getGrossAmount()
											.getCurrencyID());
							map.put("Invoice ID", resp.getPaymentTransactionDetails().getPaymentItemInfo().getInvoiceID());
							map.put("Receiver", resp.getPaymentTransactionDetails().getReceiverInfo().getReceiver());
							
							apires = new ApiResponse();
							apires.setSuccess(true);
							apires.setResponse1(map);
							return apires;

						} else {
							apires = new ApiResponse();
							apires.setSuccess(false);
							apires.setResponse1(resp.getErrors());
						}
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return apires;
	}

	
}
