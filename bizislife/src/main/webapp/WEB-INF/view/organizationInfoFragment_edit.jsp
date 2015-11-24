<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div>
    <table style="width: 90%;">
        <tbody>
            <tr id="orgedit_name" style="background-color: #909090;">
                <td class="title" style="width: 100px;">Organization Name:</td>
                <td>
                    <div class="valueSection">
                        <span id="orgname_value" class="value">${org.orgname}</span>
                    </div>
                    <c:if test="${!empty loginAccount && loginAccount.isSystemDefaultAccount() && (loginAccount.organization_id eq org.id || loginAccount.isBizAccount())}">
	                    <div class="valueEdit editSection displaynone">
	                        <form action="#" id="orgedit_name_form" style="float: left;">
	                            <input type="text" value="${org.orgname}" name="updatedValue_orgname">
	                            <input type="hidden" value="orgname" name="updatedName">
	                            <input type="hidden" value="${org.orguuid}" name="orguuid">
	                        </form>
	                    
	                    </div>
                    </c:if>
                </td>
                <td style="width: 40px; text-align: right;">
                    <c:if test="${!empty loginAccount && loginAccount.isSystemDefaultAccount() && (loginAccount.organization_id eq org.id || loginAccount.isBizAccount())}">
	                    <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="orgedit_name" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
	                    <span class="editActionIcons editSection displaynone">
	                        <img class="domReady_updateOrgInfo" 
	                            domvalue="orgedit_name" 
	                            alt="save" src="/img/vendor/web-icons/tick.png"> 
	                        <img class="domReady_cancelToEditValue" 
	                            domvalue="orgedit_name" 
	                            alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
                    </c:if>
                            
                </td>       
            </tr>

            <tr id="orgedit_addr">
                <td class="title" style="width: 100px;">Address:</td>
                <td>
                    <div class="valueSection">
                        <table style="100%;">
                            <tr>
                                <td>Apartment/Unit Number:</td>
                                <td><span id="apt_unit_number_value" class="value">${orgContact.apt_unit_number}</span></td>
                            </tr>
                            <tr>
                                <td>Address:</td>
                                <td><span id="street_number_value" class="value">${orgContact.street_number} ${orgContact.address}</span></td>
                            </tr>
                            <tr>
                                <td>City:</td>
                                <td><span id="city_value" class="value">${orgContact.city}</span></td>
                            </tr>
                            <tr>
                                <td>Province/State:</td>
                                <td><span id="state_value" class="value">${orgContact.state}</span></td>
                            </tr>
                            <tr>
                                <td>Country:</td>
                                <td><span id="country_value" class="value">${orgContact.country}</span></td>
                            </tr>
                            <tr>
                                <td>Postal/Zip:</td>
                                <td><span id="zip_value" class="value">${orgContact.zip}</span></td>
                            </tr>
                        </table>
                    </div>
                    <c:if test="${!empty loginAccount && loginAccount.isSystemDefaultAccount() && (loginAccount.organization_id eq org.id || loginAccount.isBizAccount())}">
	                    <div class="valueEdit editSection displaynone">
	                        <form action="#" id="orgedit_addr_form" style="float: left;">
	                            <table style="width: 100%;">
	                                <tr>
	                                    <td class="editAddr_title">Google address helper: </td>
	                                    <td class="editAddr_value">
	                                        <input id="autocomplete" placeholder="Enter your address" name="autocomplete" class="addrAutoComplete" type="text"/>
	                                    </td>
	                                </tr>
	                                
	                                <tr>
	                                    <td class="editAddr_title">Apartment/Unit Number: </td>
	                                    <td class="editAddr_value"><input type="text" name="updatedValue_apt_unit_number" maxlength="10" value="${orgContact.apt_unit_number}" /></td>
	                                </tr>
	                                
	                                <tr>
	                                    <td class="editAddr_title">Address:</td>
	                                    <td class="editAddr_value">
	                                        <input type="text" name="updatedValue_street_number" id="street_number" maxlength="10" value="${orgContact.street_number}" placeholder="street number" />
	                                        <input type="text" name="updatedValue_address" id="route" maxlength="255" value="${orgContact.address}" placeholder="address" />
	                                    </td>
	                                </tr>
	                                
	                                <tr>
	                                    <td class="editAddr_title">City:</td>
	                                    <td class="editAddr_value"><input type="text" name="updatedValue_city" id="locality" maxlength="255" value="${orgContact.city}" /></td>
	                                </tr>
	                                
	                                <tr>
	                                    <td class="editAddr_title">Province/State:</td>
	                                    <td class="editAddr_value"><input type="text" name="updatedValue_state" id="administrative_area_level_1" maxlength="255" value="${orgContact.state}" /></td>
	                                </tr>
	                                
	                                <tr>
	                                    <td class="editAddr_title">Country:</td>
	                                    <td class="editAddr_value"><input type="text" name="updatedValue_country" id="country" maxlength="20" value="${orgContact.country}" /></td>
	                                </tr>
	                                
	                                <tr>
	                                    <td class="editAddr_title">Postal/Zip:</td>
	                                    <td class="editAddr_value"><input type="text" name="updatedValue_zip" id="postal_code" maxlength="10" value="${orgContact.zip}" /></td>
	                                </tr>
	                                
	                                <tr>
	                                    <td class="editAddr_title">latitude:</td>
	                                    <td class="editAddr_value"><input type="text" name="updatedValue_latitude" id="latitude" value="${orgContact.latitude}" readonly="readonly"/></td>
	                                </tr>
	                                
	                                <tr>
	                                    <td class="editAddr_title">longitude:</td>
	                                    <td class="editAddr_value"><input type="text" name="updatedValue_longitude" id="longitude" value="${orgContact.longitude}" readonly="readonly" /></td>
	                                </tr>
	                            </table>
	                            
	                            <input type="hidden" value="address" name="updatedName">
	                            <input type="hidden" value="${org.orguuid}" name="orguuid">
	                        </form>
	                    
	                    </div>
                    </c:if>
                    
                </td>
                <td style="width: 40px; text-align: right;">
                    <c:if test="${!empty loginAccount && loginAccount.isSystemDefaultAccount() && (loginAccount.organization_id eq org.id || loginAccount.isBizAccount())}">
	                    <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="orgedit_addr" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
	                    <span class="editActionIcons editSection displaynone">
	                        <img class="domReady_updateOrgInfo" 
	                            domvalue="orgedit_addr" 
	                            alt="save" src="/img/vendor/web-icons/tick.png"> 
	                        <img class="domReady_cancelToEditValue" 
	                            domvalue="orgedit_addr" 
	                            alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
                    </c:if>
                </td>       
            </tr>
            
            <tr id="orgedit_phone" style="background-color: #909090;">
                <td class="title" style="width: 100px;">Phone number:</td>
                <td>
                    <div class="valueSection">
                        <span id="dayphone_country_value" class="value">(${orgContact.dayphone_country})</span> 
                        <span id="dayphone_area_value" class="value">(${orgContact.dayphone_area})</span> 
                        <span id="dayphone_value" class="value">${orgContact.dayphone}</span>
                    </div>
                    <c:if test="${!empty loginAccount && loginAccount.isSystemDefaultAccount() && (loginAccount.organization_id eq org.id || loginAccount.isBizAccount())}">
	                    <div class="valueEdit editSection displaynone">
	                        <form action="#" id="orgedit_phone_form" style="float: left;">
	                        
	                            <input type="text" name="updatedValue_dayphone_country" value="${orgContact.dayphone_country}" maxlength="6" placeholder="country code" style="width: 20%;" /> 
	                            <input type="text" name="updatedValue_dayphone_area" value="${orgContact.dayphone_area}" maxlength="6" placeholder="area code" style="width: 20%;" /> 
	                            <input type="text" name="updatedValue_dayphone" value="${orgContact.dayphone}" maxlength="20" placeholder="number" style="width: 50%;" />
	                            
	                            <input type="hidden" value="phone" name="updatedName">
	                            <input type="hidden" value="${org.orguuid}" name="orguuid">
	                        </form>
	                    
	                    </div>
                    </c:if>
                </td>
                <td style="width: 40px; text-align: right;">
                    <c:if test="${!empty loginAccount && loginAccount.isSystemDefaultAccount() && (loginAccount.organization_id eq org.id || loginAccount.isBizAccount())}">
	                    <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="orgedit_phone" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
	                    <span class="editActionIcons editSection displaynone">
	                        <img class="domReady_updateOrgInfo" 
	                            domvalue="orgedit_phone" 
	                            alt="save" src="/img/vendor/web-icons/tick.png"> 
	                        <img class="domReady_cancelToEditValue" 
	                            domvalue="orgedit_phone" 
	                            alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
                    </c:if>
                </td>       
            </tr>
            
            <tr id="orgedit_desc">
                <td class="title" style="width: 100px;">Description:</td>
                <td>
                    <div class="valueSection">
                        <span id="description_value" class="value">
                            <textarea rows="5" cols="50" name="description" disabled="disabled" readonly="readonly">${orgProfile.description}</textarea>
                        </span>
                    </div>
                    <c:if test="${!empty loginAccount && loginAccount.isSystemDefaultAccount() && (loginAccount.organization_id eq org.id || loginAccount.isBizAccount())}">
	                    <div class="valueEdit editSection displaynone">
	                        <form action="#" id="orgedit_desc_form" style="float: left;">
	                        
	                            <textarea rows="5" cols="50" name="updatedValue_description">${orgProfile.description}</textarea>
	                            
	                            <input type="hidden" value="desc" name="updatedName">
	                            <input type="hidden" value="${org.orguuid}" name="orguuid">
	                        </form>
	                    
	                    </div>
                    </c:if>
                </td>
                <td style="width: 40px; text-align: right;">
                    <c:if test="${!empty loginAccount && loginAccount.isSystemDefaultAccount() && (loginAccount.organization_id eq org.id || loginAccount.isBizAccount())}">
	                    <span class="editIcon valueSection"><img class="domReady_toEditValue" domvalue="orgedit_desc" alt="edit" src="/img/vendor/web-icons/gear--pencil.png"></span>
	                    <span class="editActionIcons editSection displaynone">
	                        <img class="domReady_updateOrgInfo" 
	                            domvalue="orgedit_desc" 
	                            alt="save" src="/img/vendor/web-icons/tick.png"> 
	                        <img class="domReady_cancelToEditValue" 
	                            domvalue="orgedit_desc" 
	                            alt="cancel" src="/img/vendor/web-icons/cross.png"></span>
                    </c:if>
                </td>       
            </tr>
            
            
            
        
        </tbody>
    </table>
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
        
    })
    
    function fillInAddress() {
        var place = autocomplete.getPlace();
        
        for (var component in component_form) {
            document.getElementById(component).value = "";
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


