package com.bigml.histogram;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CategoricalTarget extends Target<CategoricalTarget> {

  public CategoricalTarget(Object category) {
    _target = new HashMap<Object, Double>(1,1);
    _target.put(category, 1d);
  }
  
  public CategoricalTarget(HashMap<Object, Double> targetCounts) {
    _target = targetCounts;
  }
  
  public HashMap<Object, Double> getTargetCounts() {
    return _target;
  }
  
  @Override
  protected void addJSON(JSONArray binJSON, DecimalFormat format) {
    JSONObject counts = new JSONObject();
    for (Entry<Object,Double> categoryCount : _target.entrySet()) {
      Object category = categoryCount.getKey();
      double count = categoryCount.getValue();
      counts.put(category, Double.valueOf(format.format(count)));
    }
    binJSON.add(counts);
  }

  @Override
  protected CategoricalTarget combine(CategoricalTarget target) {
    HashMap<Object, Double> counts1 = getTargetCounts();
    HashMap<Object, Double> counts2 = target.getTargetCounts();
    
    HashSet<Object> categories = new HashSet<Object>();
    categories.addAll(counts1.keySet());
    categories.addAll(counts2.keySet());
    
    HashMap<Object, Double> newTargetCounts = new HashMap<Object, Double>();
    for (Object category : categories) {
      Double count1 = counts1.get(category);
      count1 = (count1 == null) ? 0 : count1;
      
      Double count2 = counts2.get(category);
      count2 = (count2 == null) ? 0 : count2;
      
      newTargetCounts.put(category, count1 + count2);
    }
    
    return new CategoricalTarget(newTargetCounts);
  }

  @Override
  protected CategoricalTarget sumUpdate(CategoricalTarget target) {
    for (Entry<Object, Double> categoryCount : target.getTargetCounts().entrySet()) {
      Object category = categoryCount.getKey();
      
      Double oldCount = _target.get(category);
      oldCount = (oldCount == null) ? 0 : oldCount;

      double newCount = oldCount + categoryCount.getValue();
      _target.put(category, newCount);
    }
    
    return this;
  }

  @Override
  protected CategoricalTarget subtractUpdate(CategoricalTarget target) {
    for (Entry<Object, Double> categoryCount : target.getTargetCounts().entrySet()) {
      Object category = categoryCount.getKey();
      
      Double oldCount = _target.get(category);
      oldCount = (oldCount == null) ? 0 : oldCount;
      
      double newCount = oldCount - categoryCount.getValue();
      _target.put(category, newCount);
    }
    
    return this;
  }

  @Override
  protected CategoricalTarget multiplyUpdate(double multiplier) {
   for (Entry<Object, Double> categoryCount : getTargetCounts().entrySet()) {
     categoryCount.setValue(categoryCount.getValue() * multiplier);
   }

   return this;
  }

  @Override
  protected CategoricalTarget clone() {
    return new CategoricalTarget(new HashMap<Object, Double>(_target));
  }

  @Override
  protected CategoricalTarget init() {
    return new CategoricalTarget(new HashMap<Object, Double>());
  }
  
  private HashMap<Object, Double> _target;
}
