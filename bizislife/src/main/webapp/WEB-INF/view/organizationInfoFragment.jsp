<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<div>
	<fieldset>
		<legend>Organization Info</legend>
		
		<table style="width: 100%;">
          <tr>
              <td class="orginfoTitle"><span class="${(!empty errorMsgs['orgname'])?'errorHighlight':''}">Organization Name *: </span></td>
              <td class="orginfoValue"><input type="text" name="orgname" value="${org.orgname}"/></td>
              <td class="orginfoErrorInfo"><span class="errorHighlight">${errorMsgs['orgname']}</span></td>
          </tr>
        
        
<%--         
          <tr>
              <td class="orginfoTitle"><span class="${(!empty errorMsgs['industry'])?'errorHighlight':''}">Industry *: </span></td>
              <td class="orginfoValue">
					<select name="industry" class="industrySelectionForSubCats" style="width: 90%;">
					    <option value="">select ...</option>
					    <c:if test="${!empty industries}">
					      <c:forEach items="${industries}" var="cata" varStatus="cataIndex">
					          <option value="${cata.key}" <c:if test="${cata.selected}"> selected="selected"</c:if>>${cata.value}</option>
					      </c:forEach>
					  </c:if>
					</select>
              </td>
              <td class="orginfoErrorInfo"><span class="errorHighlight">${errorMsgs['industry']}</span></td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span class="${(!empty errorMsgs['subcategory'])?'errorHighlight':''}">Subcategory *: </span></td>
              <td class="orginfoValue">
		            <select name="subcategory" id="subcategories" style="width: 90%;">
		                <option value="">select ...</option>
		                <c:if test="${!empty subCats}">
		                    <c:forEach items="${subCats}" var="cata" varStatus="cataIndex">
		                        <option value="${cata.key}" <c:if test="${cata.selected}"> selected="selected"</c:if>>${cata.value}</option>
		                    </c:forEach>
		                </c:if>
		            </select>
              </td>
              <td class="orginfoErrorInfo"><span class="errorHighlight">${errorMsgs['subcategory']}</span></td>
          </tr>
 --%>          
          
        
          <tr>
              <td class="orginfoTitle">Google address helper: </td>
              <td colspan="2" class="orginfoValue2">
                <input id="autocomplete" placeholder="Enter your address" name="autocomplete" class="addrAutoComplete" type="text" style="width: 50%;"/>
              </td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span>Apartment/Unit Number: </span></td>
              <td colspan="2" class="orginfoValue2"><input type="text" name="apt_unit_number" maxlength="10" value="${orgContact.apt_unit_number}" /></td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span class="${(!empty errorMsgs['address'])?'errorHighlight':''}">Address *: </span></td>
              <td class="orginfoValue">
	              <input type="text" name="street_number" id="street_number" value="${orgContact.street_number}" maxlength="10" placeholder="street number" style="width: 30%;" />
	              <input type="text" name="address" id="route" value="${orgContact.address}" maxlength="255" placeholder="address" style="width: 60%;" />
              </td>
              <td class="orginfoErrorInfo"><span class="errorHighlight">${errorMsgs['address']}</span></td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span class="${(!empty errorMsgs['city'])?'errorHighlight':''}">City *: </span></td>
              <td class="orginfoValue"><input type="text" name="city" id="locality" maxlength="255" value="${orgContact.city}" /></td>
              <td class="orginfoErrorInfo"><span class="errorHighlight">${errorMsgs['city']}</span></td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span class="${(!empty errorMsgs['state'])?'errorHighlight':''}">Province/State *: </span></td>
              <td class="orginfoValue"><input type="text" name="state" id="administrative_area_level_1" maxlength="255" value="${orgContact.state}" /></td>
              <td class="orginfoErrorInfo"><span class="errorHighlight">${errorMsgs['state']}</span></td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span class="${(!empty errorMsgs['country'])?'errorHighlight':''}">Country *: </span></td>
              <td class="orginfoValue"><input type="text" name="country" id="country" maxlength="20" value="${orgContact.country}" /></td>
              <td class="orginfoErrorInfo"><span class="errorHighlight">${errorMsgs['country']}</span></td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span class="${(!empty errorMsgs['zip'])?'errorHighlight':''}">Postal/Zip *: </span></td>
              <td class="orginfoValue"><input type="text" name="zip" id="postal_code" maxlength="10" value="${orgContact.zip}" /></td>
              <td class="orginfoErrorInfo"><span class="errorHighlight">${errorMsgs['zip']}</span></td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span>latitude: </span></td>
              <td class="orginfoValue2" colspan="2"><input type="text" name="latitude" id="latitude" value="${orgContact.latitude}" readonly="readonly"/></td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span>longitude: </span></td>
              <td class="orginfoValue2" colspan="2"><input type="text" name="longitude" id="longitude" value="${orgContact.longitude}" readonly="readonly" /></td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span>Phone Number: </span></td>
              <td class="orginfoValue2" colspan="2">
                <input type="text" name="dayphone_country" value="${orgContact.dayphone_country}" maxlength="6" placeholder="country code" style="width: 20%;" /> 
                <input type="text" name="dayphone_area" value="${orgContact.dayphone_area}" maxlength="6" placeholder="area code" style="width: 20%;" /> 
                <input type="text" name="dayphone" value="${orgContact.dayphone}" maxlength="20" placeholder="number" style="width: 50%;" />
              </td>
          </tr>
        
          <tr>
              <td class="orginfoTitle"><span>Description: </span></td>
              <td class="orginfoValue2" colspan="2">
                <textarea rows="5" cols="50" name="description">${orgProfile.description}</textarea>
              </td>
          </tr>
		</table>
	</fieldset>
	
	<%-- only open this section when in addOrganization page --%>
	<c:if test="${currentPageId eq 'addOrganization'}">
		<fieldset>
			<legend>Account Info</legend>
			<table style="width: 100%;">
	            <tr>
	                <td class="acctinfoTitle"><span class="${(!empty errorMsgs['personalEmail'])?'errorHighlight':''}">Email *: </span></td>
	                <td class="acctinfoValue"><input type="text" id="personalEmail" name="personalEmail" value="${personalEmail}" /></td>
	                <td class="acctinfoErrorInfo"><span class="errorHighlight">${errorMsgs['personalEmail']}</span></td>
	            </tr>
	            
	            <tr>
	                <td class="acctinfoTitle"><span>Use email as login name: </span></td>
	                <td class="acctinfoValue"><input type="checkbox" id="useEmailAsLoginName" name="useEmailAsLoginName" value="true" <c:if test="${!empty useEmailAsLoginName && useEmailAsLoginName}">checked="checked"</c:if> /></td>
	                <td class="acctinfoErrorInfo"><span class="errorHighlight"></span></td>
	            </tr>
	            
	            <tr>
	                <td id="loginnamePart" class="acctinfoTitle"><span class="${(!empty errorMsgs['loginname'])?'errorHighlight':''}">Login Name *: </span></td>
	                <td class="acctinfoValue"><input id="loginname" type="text" name="loginname" value="${account.loginname}" /></td>
	                <td class="acctinfoErrorInfo"><span class="errorHighlight">${errorMsgs['loginname']}</span></td>
	            </tr>
	            
	            <tr>
	                <td class="acctinfoTitle"><span class="${(!empty errorMsgs['firstname'])?'errorHighlight':''}">First Name *: </span></td>
	                <td class="acctinfoValue"><input type="text" name="firstname" value="${accountProfile.firstname}" /></td>
	                <td class="acctinfoErrorInfo"><span class="errorHighlight">${errorMsgs['firstname']}</span></td>
	            </tr>
	            
	            <tr>
	                <td class="acctinfoTitle"><span class="${(!empty errorMsgs['lastname'])?'errorHighlight':''}">Last Name *: </span></td>
	                <td class="acctinfoValue"><input type="text" name="lastname" value="${accountProfile.lastname}" /></td>
	                <td class="acctinfoErrorInfo"><span class="errorHighlight">${errorMsgs['lastname']}</span></td>
	            </tr>
			
			</table>
			
		</fieldset>
	</c:if>
	<p>Note: All fields with * are mandatory.</p>
</div>



<script type="text/javascript">
	var placeSearch,autocomplete;
	var component_form = {
		'street_number': 'short_name',
		'route': 'long_name',
		'locality': 'long_name',
		'administrative_area_level_1': 'short_name',
		'country': 'long_name',
		'postal_code': 'short_name'
	};
    $(function(){
        autocomplete = new google.maps.places.Autocomplete(document.getElementById('autocomplete'), { types: [ 'geocode' ] });
        google.maps.event.addListener(autocomplete, 'place_changed', function() {
          fillInAddress();
        });
    	
        // google place autocomplete
/*         $(".addrAutoComplete").bind("focus", function(){
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                  var geolocation = new google.maps.LatLng(position.coords.latitude,position.coords.longitude);
                  autocomplete.setBounds(new google.maps.LatLngBounds(geolocation, geolocation));
                });
              }
        })
 */
 
 		$("#useEmailAsLoginName").click(function(){
 			if($(this).attr("checked")){
 				if($.trim($("#personalEmail").val())){
 					$(this).next("span.errorHighlight").html("");
 	 				//$("#loginnamePart").hide();
 	 				$("#loginname").val($("#personalEmail").val());
 				}else{
 					$(this).attr("checked", false);
 					$(this).next("span.errorHighlight").html("emil address is empty");
 				}
 			}else{
 				//$("#loginnamePart").show();
 				$("#loginname").val("");
 			}
 		})
    })
    
	function fillInAddress() {
		var place = autocomplete.getPlace();
		
		for (var component in component_form) {
			document.getElementById(component).value = "";
//			document.getElementById(component).disabled = false;
		}
		
		for (var j = 0; j < place.address_components.length; j++) {
			var att = place.address_components[j].types[0];
			if (component_form[att]) {
				var val = place.address_components[j][component_form[att]];
				document.getElementById(att).value = val;
			}
		}

		// for latitude and longitude
		document.getElementById("latitude").value=place.geometry.location.lat();
		document.getElementById("longitude").value=place.geometry.location.lng();
		
	}    
    
</script>
