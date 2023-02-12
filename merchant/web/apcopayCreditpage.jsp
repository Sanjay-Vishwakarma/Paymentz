<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/21/15
  Time: 3:03 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="row">
  <div class="col-sm-12 portlets ui-sortable">
    <div class="widget">

      <div class="widget-header transparent">
        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Billing Address (where you receive your NetBanking Details)</strong></h2>
        <div class="additional-btn">
          <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
        </div>
      </div>

      <br>

      <div class="widget-content padding">

        <div class="form-group col-md-2">
          <label>Phone CC</label>
          <input type="text" name="telnocc" class="form-control" title="Example(Country Code - Phone Number)">
        </div>
        <div class="form-group col-md-4">
          <label>Phone Number</label>
          <input type="text" name="telno" size="20" maxlength="20" class="form-control" title="Example(Country Code - Phone Number)">
        </div>

        <div class="form-group col-md-6">
          <label>Email ID</label>
          <input type="text" name="emailaddr" title="Ex: abc@xyz.com" value="" class="form-control"/>
        </div>
      </div>
    </div>
  </div>
</div>