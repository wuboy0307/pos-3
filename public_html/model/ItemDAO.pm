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

sub now() {
    my $self = shift;
    
    my $dbh = $self->{'dbh'};
    
    my $sql = "select current_timestamp as now";
    
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    
    my ($ret, $rc, $rm);
    
    if (my $ref = $sth->fetchrow_hashref()) {
        $ret = $ref->{'now'};
    }
    $sth->finish();
        
    $dbh->disconnect();
    
    return $ret;
}

sub sync() {
    my $self = shift;
    my $deviceID = shift;

    my $now = $self->now();
    
    my $sql = "select * from item i " . 
              "where i.updateTimestamp > " .
              "(select lastTimestamp from watermark w where device_id = '$deviceID') " .
              "and i.updateTimestamp < $now";
    
    my $ret = $self->getAll($sql);
    
    $sql = "update watermark set lastTimestamp = timestamp('$now') where device_id = '$deviceID'";
    my $dbh = $self->{'dbh'};
    my $sth = $dbh->prepare($sql);
    $sth->execute();
    $sth->finish();
    $dbh->disconnect();
    
    return $ret;

}

sub reset() {
    my $self = shift;
    my $deviceID = shift;
    
    my $dbh = $self->{'dbh'};

    my $sql = "insert into watermark (device_id, lastTimestamp) " .
              "values (?, timestamp('1999-01-01 00:00:00')) " . 
              "on duplicate key update lastTimestamp=timestamp('1999-01-01 00:00:00')";
    
    $logger->debug("SQL $sql");
    my ($rm, $rc, $ret);
    my $sth = $dbh->prepare($sql);
    $sth->execute($deviceID);
    $sth->finish();
    
    $rm = "Success";
    $rc = 0;
    
    $ret->{'returnMessage'} = $rm;
    $ret->{'returnCode'} = $rc;
    
    $dbh->disconnect();
    
    return $ret;
    
}

sub getItems() {
    my $self = shift;

    my $sql = "select * from item order by description";
    
    my $ret = $self->getAll($sql);
    
    return $ret;
}

sub getAll() {
    my $self = shift;
    my $sql = shift;
    
    my $dbh = $self->{'dbh'};
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

