#!/bin/bash
#
# *******************************************************************************
# Copyright (c) 2021,2024 Contributors to the Eclipse Foundation
#
# See the NOTICE file(s) distributed with this work for additional
# information regarding copyright ownership.
#
# This program and the accompanying materials are made available under the
# terms of the Apache License, Version 2.0 which is available at
# https://www.apache.org/licenses/LICENSE-2.0.
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.
#
# SPDX-License-Identifier: Apache-2.0
# ********************************************************************************
#

# generates a combined PDF out of all all md files

BASEDIR="$(dirname "$(dirname "$(readlink -fm "$0")")")"

echo $BASEDIR
docker build --no-cache --file "$BASEDIR"/pandoc.Dockerfile -t tractus-x/pandoc-generator:latest "$BASEDIR"
containerId=$(docker create tractus-x/pandoc-generator:latest)
docker cp "$containerId":/data/Documentation.pdf "$BASEDIR"
