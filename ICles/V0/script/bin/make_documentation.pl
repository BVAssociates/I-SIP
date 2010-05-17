#!/usr/bin/env perl

use strict;
use Pod::Simple::HTMLBatch;


my @search_dirs=("$ENV{ISIP_HOME}/V0/Core/");
my $output_dir='D:\ISIP\trac_server\htdocs\isip-doc';

my $batchconv = Pod::Simple::HTMLBatch->new;
$batchconv->add_css("/trac_server/chrome/common/css/trac.css",1);
$batchconv->batch_convert( \@search_dirs, $output_dir );