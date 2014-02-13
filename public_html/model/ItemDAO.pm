package ItemDAO;
use DB;
our @ISA = qw(DB);    # inherits from DB

############################################
#
#   Filename:  DAO.pm (package DAO)
#   Author: 
#
############################################

use strict;
use DBI;
use Log::Log4perl;
use Sys::Hostname;
use Data::Dumper;
use String::Util 'trim';

my $logger = Log::Log4perl->get_logger('opuma');

sub getItems() {
    my $self = shift;
    
    my $dbh = $self->{'dbh'};

    my $sql = "select * from item order by description";
    
    my $sth = $dbh->prepare($sql);
    $sth->execute();

    my ($ret, $rc, $rm, $ref, @a, $count);

    $count = 0;
    while ($ref = $sth->fetchrow_hashref()) {
        push @a, $ref;
        $count++;
    }
    $sth->finish();
    
    $rm = "Success: $count";
    $rc = 0;
    
    $ret->{'returnMessage'} = $rm;
    $ret->{'returnCode'} = $rc;
    $ret->{'data'} = \@a;
    
    $dbh->disconnect();
    
    return $ret;
}