#!/usr/bin/perl

use lib qw(/home/Students/g_m108/perllib);
use CGI;

my $cgi = new CGI();
print $cgi->header('text/html');

print "<html><body><p>hello</p></body></html>\n";

0;
