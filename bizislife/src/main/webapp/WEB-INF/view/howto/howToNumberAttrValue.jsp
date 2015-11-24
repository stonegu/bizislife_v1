<div class="methodDetail" style="border: 1px solid;">
    <h3 style="margin: 3px;">Method Detail For Module</h3>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>module.getPrettyName</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getPrettyName()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                This method only works for product, which return product name defined in product tree.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>module.getType</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getType()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                This method only works for product, which return type of product ('folder' or 'entity').
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>module.getAttrGroupList</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public List&lt;AttrGroup&gt; getAttrGroupList()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                List of <strong>AttrGroup</strong> objects.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>module.getGroupByUuid</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public AttrGroup getGroupByUuid(String groupUuid)
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                groupUuid - group's uuid.
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                Null or <strong>AttrGroup</strong> object.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>module.getGroups</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public Map&lt;String, List&lt;AttrGroup&gt;&gt; getGroups()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                A map which key is group's name, and value is a list of <strong>AttrGroup</strong> objects with same group name.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>module.getModuleAttributeByUuid</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public ModuleAttribute getModuleAttributeByUuid(String attrUuid)
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                attrUuid - attribute's uuid.
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                Null or a <strong>ModuleAttribute</strong>'s object.
            </div>
        </div>
    </div>
    
    <h3 style="margin: 3px;">Method Detail For AttrGroup</h3>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attrGroup.getArray</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public Boolean getArray()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                true - group can be duplicated.<br/>
                false - group can't be duplicated.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attrGroup.getAttrList</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public List&lt;ModuleAttribute&gt; getAttrList()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                A list of <strong>ModuleAttribute</strong> objects
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attrGroup.getAttrs</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public Map&lt;String, List&lt;ModuleAttribute&gt;&gt; getAttrs()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                A map which key is attribute's name, value is a list of <strong>ModuleAttribute</strong> objects with same attribute name.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attrGroup.getGroupName</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getGroupName()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                AttrGroup's name.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attrGroup.getModuleAttributeByUuid</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public ModuleAttribute getModuleAttributeByUuid(String attrUuid)
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                attrUuid - attribute's uuid
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                Null or a <strong>ModuleAttribute</strong>'s object.
            </div>
        </div>
    </div>
    
    <h3 style="margin: 3px;">Method Detail For ModuleAttribute</h3>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attr.getArray</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public Boolean getArray()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                true - attribute can be duplicated.<br/>
                false - attribute can't be duplicated.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attr.getName</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getName()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                Attribute's name.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attr.getTitle</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getTitle()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                Attribute's title.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attr.getMaxValue</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public BigDecimal getMaxValue()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                Maximum value can this number have.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attr.getMinValue</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public BigDecimal getMinValue()
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                Minimum value can this number have.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attr.getValue</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public BigDecimal getValue(){
        </div>
        <div class="params" style="padding: 2px 6px;">
            <div><strong>Parameters:</strong></div>
            <div style="padding: 2px 6px;">
                None
            </div>
        </div>
        <div class="returns" style="padding: 2px 6px;">
            <div><strong>Returns:</strong></div>
            <div style="padding: 2px 6px;">
                This attribute value.
            </div>
        </div>
    </div>
    
    
</div>