<div class="methodDetail" style="border: 1px solid;">
    <h3 style="margin: 3px;">Method Detail For Module</h3>
    
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
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>attr.getTotalNumProductsInPage</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public Integer getTotalNumProductsInPage()
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
                How many products can be in one page.
            </div>
        </div>
    </div>
    
    <h3 style="margin: 3px;">Method Detail For Pagination</h3>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getCurrentNode</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public PaginationNode getCurrentNode()
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
                Current selected node.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getExtraInfo</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getExtraInfo()
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
                Extra information for pagination. For example, current status: 
                "showing 6-10 of 20"
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getGoFirstNode</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public PaginationNode getGoFirstNode()
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
                Go first node.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getGoLastNode</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public PaginationNode getGoLastNode()
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
                Go last node.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getGoNextNode</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public PaginationNode getGoNextNode()
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
                Go next node.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getGoPreviousNode</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public PaginationNode getGoPreviousNode()
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
                Go previous node.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getPageNumberList</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public List<PaginationNode> getPageNumberList()
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
                A list of PaginationNodes from 1 to n (without 'first', 'previous', 'next', 'last', ...).
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getPaginationNodes</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public List<PaginationNode> getPaginationNodes()
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
                A list of all nodes.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getViewAllUrl</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getViewAllUrl()
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
                A link for view all.
            </div>
        </div>
    </div>

    <h3 style="margin: 3px;">Method Detail For PaginationNode</h3>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getPrettyName</h4></div>
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
                Node's prettyname.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getTitle</h4></div>
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
                Node's title.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getType</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public PaginationNodeType getType()
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
                Types: goFirst, goPrevious, goNext, goLast, dotdotdot, number.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.getUrl</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getUrl()
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
                Node's url.
            </div>
        </div>
    </div>

    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>pagination.isCurrentNode</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public boolean isCurrentNode()
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
                true or false.
            </div>
        </div>
    </div>

    <h3 style="margin: 3px;">Method Detail For ProductTree (EntityTreeNode)</h3>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>productTree.getDefaultImageSysName</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getDefaultImageSysName()
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
                Image system name if category or product has default image defined.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>productTree.getPrettyName</h4></div>
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
                Node's prettyname.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>productTree.getSubnodes</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public List<EntityTreeNode> getSubnodes()
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
                List of child nodes.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>productTree.getSystemName</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getSystemName()
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
                Node's system name.
            </div>
        </div>
    </div>
    
    <div class="method" style="border: 1px solid; margin: 3px;">
        <div class="methodHead" style="background-color: #DEE3E9; border-bottom: 1px solid; padding: 2px 4px;"><h4>productTree.getUrl</h4></div>
        <div class="interface" style="padding: 2px 6px;">
            public String getUrl()
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
                Node's url.
            </div>
        </div>
    </div>
    
</div>