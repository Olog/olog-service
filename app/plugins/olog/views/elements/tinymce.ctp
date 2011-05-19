<?php echo $this->Html->script("tiny_mce/tiny_mce.js"); ?> 
<script language="javascript" type="text/javascript"> 
<?php if($preset = "basic") 
{ 
    $options = ' 
    mode : "textareas", 
    theme : "advanced", 
    theme_advanced_buttons1 : "bold,italic,underline,separator,justifyleft,justifycenter,justifyright, justifyfull,bullist,numlist,undo,redo,link,unlink",
    theme_advanced_buttons2 : "", 
    theme_advanced_buttons3 : "", 
    theme_advanced_toolbar_location : "top", 
    theme_advanced_toolbar_align : "left", 
    theme_advanced_path_location : "bottom", 
    extended_valid_elements : "a[name|href|target|title|onclick],img[class|src|border=0|alt|title|hspace|vspace|width|height|align|onmouseover|onmouseout|name],hr[class|width|size|noshade],font[face|size|color|style],span[class|align|style]",
    content_css : "/css/'.$this->layout.'.css"     
    '; 
} 
?> 
tinyMCE.init({<?php echo($options); ?>}); 
</script> 